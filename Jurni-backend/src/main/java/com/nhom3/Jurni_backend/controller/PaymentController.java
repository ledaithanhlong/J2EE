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
    private final VoucherRepository voucherRepository;

    public PaymentController(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              VoucherRepository voucherRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.voucherRepository = voucherRepository;
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
            String voucherCode = (String) body.get("voucherCode");

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

            // ── Voucher processing ──────────────────────────────────────────
            double discountAmount = 0;
            Map<String, Object> voucherInfo = null;

            if (voucherCode != null && !voucherCode.isBlank()) {
                Voucher voucher = voucherRepository.findByCode(voucherCode.trim().toUpperCase()).orElse(null);
                if (voucher != null && Boolean.TRUE.equals(voucher.getIsActive())) {
                    Instant now = Instant.now();
                    boolean expired = voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(now);
                    boolean notStarted = voucher.getStartDate() != null && voucher.getStartDate().isAfter(now);
                    int used = voucher.getCurrentUsage() != null ? voucher.getCurrentUsage() : 0;
                    boolean overLimit = voucher.getUsageLimit() != null && voucher.getUsageLimit() > 0
                                       && used >= voucher.getUsageLimit();

                    if (!expired && !notStarted && !overLimit) {
                        // Calculate discount on subtotal (before fee)
                        if (voucher.getDiscountPercent() != null && voucher.getDiscountPercent() > 0) {
                            discountAmount = amount * voucher.getDiscountPercent() / 100.0;
                            if (voucher.getMaxDiscount() != null && voucher.getMaxDiscount() > 0) {
                                discountAmount = Math.min(discountAmount, voucher.getMaxDiscount());
                            }
                        } else if (voucher.getDiscountAmount() != null) {
                            discountAmount = voucher.getDiscountAmount();
                        }
                        discountAmount = Math.min(discountAmount, amount);
                        discountAmount = Math.round(discountAmount);

                        // Increase usage count
                        voucher.setCurrentUsage(used + 1);
                        voucherRepository.save(voucher);

                        voucherInfo = Map.of(
                            "code", voucher.getCode(),
                            "discount_amount", discountAmount
                        );
                    }
                }
            }
            // ────────────────────────────────────────────────────────────────

            double finalAmount = Math.max(0, amount - discountAmount + fee);

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

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "Thanh toán thành công. Hóa đơn đã được gửi tới email của bạn.");
            response.put("payment", Map.of(
                "reference", transactionRef,
                "status", "succeeded",
                "method", method.get("id"),
                "amount", amount,
                "discount_amount", discountAmount,
                "fee", Math.round(fee),
                "final_amount", Math.round(finalAmount),
                "currency", body.getOrDefault("currency", "VND"),
                "processedAt", Instant.now().toString()
            ));
            response.put("booking", createdBookings);
            response.put("customer", customer);
            response.put("items", items);
            if (voucherInfo != null) response.put("voucher", voucherInfo);

            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Support for PaymentPage /api/payments/checkout alias ────────────────
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> body) {
        return processPayment(body);
    }
    // ────────────────────────────────────────────────────────────────────────

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
