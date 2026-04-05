package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.*;
import com.nhom3.Jurni_backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public PaymentController(BookingRepository bookingRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    private static final List<Map<String, Object>> PAYMENT_METHODS = List.of(
        Map.of("id","card","name","Thẻ quốc tế (Visa / Mastercard)","type","card",
               "feePercent",0.018,"feeFixed",0,
               "description","Thanh toán tức thời với thẻ Visa, Mastercard hoặc JCB."),
        Map.of("id","momo","name","Ví điện tử MoMo","type","ewallet",
               "feePercent",0.012,"feeFixed",2000,
               "description","Quét mã QR hoặc xác nhận trên ứng dụng MoMo."),
        Map.of("id","zalopay","name","Ví ZaloPay","type","ewallet",
               "feePercent",0.01,"feeFixed",1500,
               "description","Xác nhận giao dịch qua ứng dụng ZaloPay."),
        Map.of("id","bank_transfer","name","Chuyển khoản ngân hàng","type","bank",
               "feePercent",0.0,"feeFixed",0,
               "description","Miễn phí. Hoàn tất trong vòng 15 phút kể từ khi nhận tiền.")
    );

    @GetMapping("/config")
    public ResponseEntity<?> getPaymentConfig() {
        return ResponseEntity.ok(Map.of(
            "currency", "VND",
            "paymentMethods", PAYMENT_METHODS,
            "bankAccount", Map.of(
                "name", "CÔNG TY TNHH DU LỊCH JURNI",
                "bank", "Vietcombank - CN Tân Định",
                "accountNumber", "0451 2345 6789"
            ),
            "notes", "Phí giao dịch có thể thay đổi tùy theo ngân hàng phát hành."
        ));
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> body) {
        try {
            Object amountObj = body.get("amount");
            String paymentMethodId = (String) body.get("paymentMethod");
            @SuppressWarnings("unchecked")
            Map<String, Object> customer = (Map<String, Object>) body.get("customer");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.getOrDefault("items", List.of());

            if (amountObj == null || Double.parseDouble(amountObj.toString()) <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số tiền thanh toán không hợp lệ."));
            }
            if (paymentMethodId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng chọn phương thức thanh toán."));
            }

            Map<String, Object> method = PAYMENT_METHODS.stream()
                .filter(m -> m.get("id").equals(paymentMethodId))
                .findFirst()
                .orElse(null);
            if (method == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phương thức thanh toán không được hỗ trợ."));
            }

            if (customer == null || customer.get("name") == null || customer.get("email") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin liên hệ của khách hàng."));
            }

            double amount = Double.parseDouble(amountObj.toString());
            double feePercent = ((Number) method.get("feePercent")).doubleValue();
            double feeFixed = ((Number) method.get("feeFixed")).doubleValue();
            double fee = amount * feePercent + feeFixed;
            String transactionRef = "PAY-" + System.currentTimeMillis();

            // Find or create system user
            String systemUserId = userRepository.findByEmail("system@jurni.com")
                .map(User::getId)
                .orElseGet(() -> {
                    User sys = new User();
                    sys.setName("System User");
                    sys.setEmail("system@jurni.com");
                    sys.setRole("user");
                    return userRepository.save(sys).getId();
                });

            List<Booking> createdBookings = new ArrayList<>();

            if (!items.isEmpty()) {
                for (Map<String, Object> item : items) {
                    String itemId = (String) item.get("id");
                    String itemType = (String) item.get("type");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> details = (Map<String, Object>) item.getOrDefault("details", Map.of());

                    String serviceType = detectServiceType(itemType, itemId);
                    if (serviceType == null) continue;

                    String refId = extractRefId(itemId);

                    Booking booking = new Booking();
                    booking.setUserId(systemUserId);
                    booking.setServiceType(serviceType);
                    booking.setTotalPrice(parseDouble(item.get("price"), 0) * parseInt(item.get("quantity"), 1));
                    booking.setStatus("pending");
                    booking.setCustomerName((String) customer.get("name"));
                    booking.setCustomerEmail((String) customer.get("email"));
                    booking.setCustomerPhone((String) customer.get("phone"));
                    booking.setPaymentMethod((String) method.get("name"));
                    booking.setTransactionId(transactionRef);
                    booking.setQuantity(parseInt(item.get("quantity"), 1));
                    booking.setItemVariant(getVariant(details));

                    setServiceDate(booking, details);
                    setServiceRef(booking, serviceType, refId);

                    createdBookings.add(bookingRepository.save(booking));
                }
            }

            return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "message", "Thanh toán thành công. Hóa đơn đã được gửi tới email của bạn.",
                "payment", Map.of(
                    "reference", transactionRef,
                    "status", "succeeded",
                    "method", method.get("id"),
                    "amount", amount,
                    "currency", body.getOrDefault("currency", "VND"),
                    "fee", Math.round(fee),
                    "processedAt", Instant.now().toString()
                ),
                "booking", createdBookings,
                "customer", customer,
                "items", items
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    private String detectServiceType(String type, String id) {
        if (type == null && id == null) return null;
        String t = type != null ? type.toLowerCase() : "";
        String i = id != null ? id.toLowerCase() : "";
        if (t.contains("hotel") || t.contains("khách sạn") || i.contains("hotel")) return "hotel";
        if (t.contains("flight") || t.contains("chuyến bay") || i.contains("flight")) return "flight";
        if (t.contains("car") || t.contains("thuê xe") || i.contains("car")) return "car";
        if (t.contains("activity") || t.contains("hoạt động") || i.contains("activity")) return "activity";
        return null;
    }

    private String extractRefId(String id) {
        if (id == null) return null;
        String[] parts = id.split("-");
        return parts.length > 1 ? parts[1] : id;
    }

    private String getVariant(Map<String, Object> details) {
        for (String key : new String[]{"roomType","ticketType","ticketClass","carType","activityType"}) {
            if (details.containsKey(key)) return (String) details.get(key);
        }
        return null;
    }

    private void setServiceDate(Booking booking, Map<String, Object> details) {
        for (String key : new String[]{"checkIn","startDate","pickupDate","departureTime","date"}) {
            if (details.containsKey(key) && details.get(key) != null) {
                try { booking.setStartDate(Instant.parse(details.get(key).toString())); break; } catch (Exception ignored) {}
            }
        }
        for (String key : new String[]{"checkOut","endDate","dropoffDate","arrivalTime"}) {
            if (details.containsKey(key) && details.get(key) != null) {
                try { booking.setEndDate(Instant.parse(details.get(key).toString())); break; } catch (Exception ignored) {}
            }
        }
    }

    private void setServiceRef(Booking booking, String serviceType, String refId) {
        switch (serviceType) {
            case "hotel" -> booking.setHotelId(refId);
            case "flight" -> booking.setFlightId(refId);
            case "car" -> booking.setCarId(refId);
            case "activity" -> booking.setActivityId(refId);
        }
    }

    private double parseDouble(Object val, double def) {
        if (val == null) return def;
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return def; }
    }

    private int parseInt(Object val, int def) {
        if (val == null) return def;
        try { return (int) Double.parseDouble(val.toString()); } catch (Exception e) { return def; }
    }
}
