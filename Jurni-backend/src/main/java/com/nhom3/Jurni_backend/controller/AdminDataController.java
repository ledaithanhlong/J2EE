package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.config.DataLoader;
import com.nhom3.Jurni_backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin endpoint for managing database seed data
 * Endpoints:
 * - GET /api/admin/seed-data - Seed database with sample data (only if empty)
 * - DELETE /api/admin/clear-data - Clear all data
 * - GET /api/admin/stats - Show database record counts
 */
@RestController
@RequestMapping("/api/admin")
public class AdminDataController {

    private final DataLoader dataLoader;
    private final HotelRepository hotelRepo;
    private final CarRepository carRepo;
    private final ActivityRepository activityRepo;
    private final VoucherRepository voucherRepo;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;

    public AdminDataController(
            DataLoader dataLoader,
            HotelRepository hotelRepo,
            CarRepository carRepo,
            ActivityRepository activityRepo,
            VoucherRepository voucherRepo,
            BookingRepository bookingRepo,
            UserRepository userRepo) {
        this.dataLoader = dataLoader;
        this.hotelRepo = hotelRepo;
        this.carRepo = carRepo;
        this.activityRepo = activityRepo;
        this.voucherRepo = voucherRepo;
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
    }

    /**
     * Get current database statistics
     * Usage: GET http://localhost:5000/api/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Long> stats = Map.ofEntries(
                Map.entry("hotels", hotelRepo.count()),
                Map.entry("cars", carRepo.count()),
                Map.entry("activities", activityRepo.count()),
                Map.entry("vouchers", voucherRepo.count()),
                Map.entry("bookings", bookingRepo.count()),
                Map.entry("users", userRepo.count())
        );
        
        System.out.println("📊 Database Stats: " + stats);
        return ResponseEntity.ok(Map.of(
                "message", "Current database statistics",
                "data", stats,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Seed database with sample data (only seeds empty collections)
     * Usage: GET http://localhost:5000/api/admin/seed-data
     * 
     * Response: {
     *   "message": "Data seeding completed",
     *   "seeded": {"hotels": 9, "cars": 8, "activities": 6, "vouchers": 4},
     *   "skipped": {"hotels": 0} // If already has data
     * }
     */
    @GetMapping("/seed-data")
    public ResponseEntity<?> seedData() {
        System.out.println("\n🌱 Manual seed request received...\n");
        
        Map<String, Object> seeded = Map.ofEntries(
                Map.entry("hotels", hotelRepo.count() == 0 ? 9 : 0),
                Map.entry("cars", carRepo.count() == 0 ? 8 : 0),
                Map.entry("activities", activityRepo.count() == 0 ? 6 : 0),
                Map.entry("vouchers", voucherRepo.count() == 0 ? 4 : 0)
        );

        try {
            // Trigger seeding
            dataLoader.seedAll(hotelRepo, carRepo, activityRepo, voucherRepo);
            
            return ResponseEntity.ok(Map.of(
                    "message", "✅ Data seeding completed successfully!",
                    "seeded", seeded,
                    "hint", "Data will only seed if collections are empty. Use /api/admin/clear-data to reset.",
                    "next_step", "Refresh admin pages to see new data"
            ));
        } catch (Exception e) {
            System.err.println("❌ Error during seeding: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Seeding failed: " + e.getMessage(),
                    "type", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * Clear ALL data from database
     * ⚠️ WARNING: This deletes everything except user data from Clerk
     * Usage: DELETE http://localhost:5000/api/admin/clear-data
     * 
     * Response: {
     *   "message": "Database cleared",
     *   "deleted": {"hotels": 9, "cars": 8, "activities": 6, "vouchers": 4, "bookings": 0}
     * }
     */
    @DeleteMapping("/clear-data")
    public ResponseEntity<?> clearData(@RequestParam(defaultValue = "false") boolean confirm) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "warning", "⚠️ This will delete ALL hotels, cars, activities, vouchers, and bookings!",
                    "instruction", "To confirm deletion, add query parameter: ?confirm=true",
                    "command", "curl -X DELETE 'http://localhost:5000/api/admin/clear-data?confirm=true'",
                    "next_step", "After clearing, use GET /api/admin/seed-data to reseed"
            ));
        }

        try {
            long hotelsDeleted = hotelRepo.count();
            long carsDeleted = carRepo.count();
            long activitiesDeleted = activityRepo.count();
            long vouchersDeleted = voucherRepo.count();
            long bookingsDeleted = bookingRepo.count();

            System.out.println("🗑️ Clearing database...");
            hotelRepo.deleteAll();
            carRepo.deleteAll();
            activityRepo.deleteAll();
            voucherRepo.deleteAll();
            bookingRepo.deleteAll();

            System.out.println("✅ Database cleared");

            return ResponseEntity.ok(Map.of(
                    "message", "✅ Database cleared successfully!",
                    "deleted", Map.of(
                            "hotels", hotelsDeleted,
                            "cars", carsDeleted,
                            "activities", activitiesDeleted,
                            "vouchers", vouchersDeleted,
                            "bookings", bookingsDeleted
                    ),
                    "next_step", "Use GET /api/admin/seed-data to reseed"
            ));
        } catch (Exception e) {
            System.err.println("❌ Error during clearing: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Clearing failed: " + e.getMessage(),
                    "type", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * Clear AND reseed data in one call
     * Usage: POST http://localhost:5000/api/admin/reset-data?confirm=true
     * 
     * Response: {
     *   "message": "Database reset and reseeded",
     *   "deleted": {...},
     *   "seeded": {...}
     * }
     */
    @PostMapping("/reset-data")
    public ResponseEntity<?> resetData(@RequestParam(defaultValue = "false") boolean confirm) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "warning", "⚠️ This will DELETE all data and reseed fresh data!",
                    "instruction", "To confirm, add query parameter: ?confirm=true"
            ));
        }

        try {
            System.out.println("\n🔄 Resetting database (clear + reseed)...\n");

            // Clear all data
            long hotelsDeleted = hotelRepo.count();
            long carsDeleted = carRepo.count();
            long activitiesDeleted = activityRepo.count();
            long vouchersDeleted = voucherRepo.count();
            long bookingsDeleted = bookingRepo.count();

            hotelRepo.deleteAll();
            carRepo.deleteAll();
            activityRepo.deleteAll();
            voucherRepo.deleteAll();
            bookingRepo.deleteAll();

            System.out.println("✅ Old data deleted\n");

            // Reseed
            dataLoader.seedAll(hotelRepo, carRepo, activityRepo, voucherRepo);

            return ResponseEntity.ok(Map.of(
                    "message", "✅ Database reset and reseeded successfully!",
                    "deleted", Map.of(
                            "hotels", hotelsDeleted,
                            "cars", carsDeleted,
                            "activities", activitiesDeleted,
                            "vouchers", vouchersDeleted,
                            "bookings", bookingsDeleted
                    ),
                    "seeded", Map.of(
                            "hotels", 9,
                            "cars", 8,
                            "activities", 6,
                            "vouchers", 4
                    ),
                    "next_step", "Refresh admin pages or reload browser"
            ));
        } catch (Exception e) {
            System.err.println("❌ Error during reset: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Reset failed: " + e.getMessage(),
                    "type", e.getClass().getSimpleName()
            ));
        }
    }
}
