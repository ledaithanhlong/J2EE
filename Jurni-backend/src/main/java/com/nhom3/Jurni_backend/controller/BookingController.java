package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.*;
import com.nhom3.Jurni_backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final FlightRepository flightRepository;
    private final CarRepository carRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public BookingController(BookingRepository bookingRepository,
                             HotelRepository hotelRepository,
                             FlightRepository flightRepository,
                             CarRepository carRepository,
                             ActivityRepository activityRepository,
                             UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.flightRepository = flightRepository;
        this.carRepository = carRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    // GET /api/bookings?clerkId=xxx (user sees own, admin sees all)
    @GetMapping
    public ResponseEntity<?> getAllBookings(
            @RequestParam(required = false) String clerkId) {
        List<Booking> bookings;

        if (clerkId != null) {
            // Check if admin
            Optional<User> userOpt = userRepository.findByClerkId(clerkId);
            if (userOpt.isPresent() && "admin".equals(userOpt.get().getRole())) {
                bookings = bookingRepository.findAllByOrderByCreatedAtDesc();
            } else {
                String userId = userOpt.map(User::getId).orElse(null);
                bookings = userId != null
                    ? bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                    : List.of();
            }
        } else {
            bookings = bookingRepository.findAllByOrderByCreatedAtDesc();
        }

        List<Map<String, Object>> result = bookings.stream()
            .map(this::enrichBooking)
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // GET /api/bookings/:id
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable String id,
                                         @RequestParam(required = false) String clerkId) {
        return bookingRepository.findById(id).map(booking -> {
            if (clerkId != null) {
                Optional<User> userOpt = userRepository.findByClerkId(clerkId);
                boolean isAdmin = userOpt.map(u -> "admin".equals(u.getRole())).orElse(false);
                String userId = userOpt.map(User::getId).orElse(null);
                if (!isAdmin && !booking.getUserId().equals(userId)) {
                    return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
                }
            }
            return ResponseEntity.ok(enrichBooking(booking));
        }).orElse(ResponseEntity.notFound().build());
    }

    // POST /api/bookings
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.status(201).body(saved);
    }

    // PUT /api/bookings/:id (update status)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable String id,
                                            @RequestBody Map<String, Object> body) {
        return bookingRepository.findById(id).map(booking -> {
            if (body.containsKey("status")) booking.setStatus((String) body.get("status"));
            return ResponseEntity.ok(bookingRepository.save(booking));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/bookings/:id (admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable String id,
                                            @RequestParam(required = false) String clerkId) {
        // Check admin
        if (clerkId != null) {
            Optional<User> userOpt = userRepository.findByClerkId(clerkId);
            boolean isAdmin = userOpt.map(u -> "admin".equals(u.getRole())).orElse(false);
            if (!isAdmin) {
                return ResponseEntity.status(403).body(Map.of("error", "Only admins can delete bookings"));
            }
        }
        return bookingRepository.findById(id).map(booking -> {
            bookingRepository.delete(booking);
            return ResponseEntity.ok(Map.of("success", true, "message", "Booking deleted successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Helper: enrich booking with referenced service data
    private Map<String, Object> enrichBooking(Booking b) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", b.getId());
        map.put("userId", b.getUserId());
        map.put("hotelId", b.getHotelId());
        map.put("flightId", b.getFlightId());
        map.put("carId", b.getCarId());
        map.put("activityId", b.getActivityId());
        map.put("startDate", b.getStartDate());
        map.put("endDate", b.getEndDate());
        map.put("quantity", b.getQuantity());
        map.put("itemVariant", b.getItemVariant());
        map.put("item_variant", b.getItemVariant());
        map.put("totalPrice", b.getTotalPrice());
        map.put("total_price", b.getTotalPrice());
        map.put("status", b.getStatus());
        map.put("customerName", b.getCustomerName());
        map.put("customer_name", b.getCustomerName());
        map.put("customerEmail", b.getCustomerEmail());
        map.put("customer_email", b.getCustomerEmail());
        map.put("customerPhone", b.getCustomerPhone());
        map.put("customer_phone", b.getCustomerPhone());
        map.put("paymentMethod", b.getPaymentMethod());
        map.put("payment_method", b.getPaymentMethod());
        map.put("transactionId", b.getTransactionId());
        map.put("transaction_id", b.getTransactionId());
        map.put("createdAt", b.getCreatedAt());
        map.put("updatedAt", b.getUpdatedAt());

        // Resolve service references
        Object hotel = null, flight = null, car = null, activity = null;
        String serviceType = b.getServiceType();

        if (b.getHotelId() != null) {
            hotel = hotelRepository.findById(b.getHotelId()).orElse(null);
            serviceType = "hotel";
        }
        if (b.getFlightId() != null) {
            flight = flightRepository.findById(b.getFlightId()).orElse(null);
            serviceType = "flight";
        }
        if (b.getCarId() != null) {
            car = carRepository.findById(b.getCarId()).orElse(null);
            serviceType = "car";
        }
        if (b.getActivityId() != null) {
            activity = activityRepository.findById(b.getActivityId()).orElse(null);
            serviceType = "activity";
        }

        map.put("hotel", hotel);
        map.put("flight", flight);
        map.put("car", car);
        map.put("activity", activity);
        map.put("service_type", serviceType);
        map.put("serviceType", serviceType);

        Object service = hotel != null ? hotel : flight != null ? flight : car != null ? car : activity;
        map.put("service", service != null ? service : Map.of());

        // User info
        if (b.getUserId() != null) {
            userRepository.findById(b.getUserId()).ifPresent(u -> {
                map.put("user", Map.of("id", u.getId(), "name", u.getName(), "email", u.getEmail()));
            });
        }

        return map;
    }
}
