package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Voucher;
import com.nhom3.Jurni_backend.repository.VoucherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherRepository voucherRepository;

    public VoucherController(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    // GET /api/vouchers — list all, or only active ones with ?active=true
    @GetMapping
    public ResponseEntity<List<Voucher>> listVouchers(
            @RequestParam(required = false) Boolean active) {
        List<Voucher> all = voucherRepository.findAll();
        if (Boolean.TRUE.equals(active)) {
            Instant now = Instant.now();
            all = all.stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
                .filter(v -> v.getExpiryDate() == null || v.getExpiryDate().isAfter(now))
                .filter(v -> v.getStartDate() == null || v.getStartDate().isBefore(now))
                .collect(Collectors.toList());
        }
        return ResponseEntity.ok(all);
    }

    // GET /api/vouchers/:id
    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucher(@PathVariable String id) {
        return voucherRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/vouchers/code/:code
    @GetMapping("/code/{code}")
    public ResponseEntity<?> findByCode(@PathVariable String code) {
        return voucherRepository.findByCode(code)
            .map(v -> ResponseEntity.ok((Object) v))
            .orElse(ResponseEntity.status(404).body(Map.of("error", "Voucher không tồn tại")));
    }

    // POST /api/vouchers/apply — validate voucher and return discount
    @PostMapping("/apply")
    public ResponseEntity<?> applyVoucher(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        Object subtotalObj = body.get("subtotal");

        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng nhập mã giảm giá"));
        }

        double subtotal = 0;
        try {
            subtotal = subtotalObj != null ? Double.parseDouble(subtotalObj.toString()) : 0;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Giá trị đơn hàng không hợp lệ"));
        }

        Voucher voucher = voucherRepository.findByCode(code.trim().toUpperCase()).orElse(null);
        if (voucher == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Mã giảm giá không hợp lệ"));
        }

        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mã giảm giá đã bị vô hiệu hóa"));
        }

        Instant now = Instant.now();
        if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(now)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mã giảm giá chưa đến thời gian hiệu lực"));
        }

        if (voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(now)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mã giảm giá đã hết hạn"));
        }

        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() > 0) {
            int used = voucher.getCurrentUsage() != null ? voucher.getCurrentUsage() : 0;
            if (used >= voucher.getUsageLimit()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã giảm giá đã hết lượt sử dụng"));
            }
        }

        if (voucher.getMinSpend() != null && voucher.getMinSpend() > 0 && subtotal < voucher.getMinSpend()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", String.format("Đơn hàng tối thiểu %,.0f VND để áp dụng mã này", voucher.getMinSpend())
            ));
        }

        // Calculate discount
        double discount = 0;
        if (voucher.getDiscountPercent() != null && voucher.getDiscountPercent() > 0) {
            discount = subtotal * voucher.getDiscountPercent() / 100.0;
            if (voucher.getMaxDiscount() != null && voucher.getMaxDiscount() > 0) {
                discount = Math.min(discount, voucher.getMaxDiscount());
            }
        } else if (voucher.getDiscountAmount() != null && voucher.getDiscountAmount() > 0) {
            discount = voucher.getDiscountAmount();
        }
        discount = Math.min(discount, subtotal);
        discount = Math.round(discount);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "voucher", voucher,
            "discount_amount", discount,
            "final_amount", Math.max(0, subtotal - discount)
        ));
    }

    // POST /api/vouchers — create new voucher
    @PostMapping
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {
        if (voucher.getCode() != null) {
            voucher.setCode(voucher.getCode().trim().toUpperCase());
        }
        if (voucher.getCurrentUsage() == null) voucher.setCurrentUsage(0);
        if (voucher.getIsActive() == null) voucher.setIsActive(true);
        return ResponseEntity.status(201).body(voucherRepository.save(voucher));
    }

    // PUT /api/vouchers/:id — update voucher
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoucher(@PathVariable String id, @RequestBody Voucher body) {
        return voucherRepository.findById(id).map(v -> {
            if (body.getCode() != null) v.setCode(body.getCode().trim().toUpperCase());
            if (body.getDescription() != null) v.setDescription(body.getDescription());
            if (body.getDiscountPercent() != null) v.setDiscountPercent(body.getDiscountPercent());
            if (body.getDiscountAmount() != null) v.setDiscountAmount(body.getDiscountAmount());
            if (body.getMinSpend() != null) v.setMinSpend(body.getMinSpend());
            if (body.getMaxDiscount() != null) v.setMaxDiscount(body.getMaxDiscount());
            if (body.getStartDate() != null) v.setStartDate(body.getStartDate());
            if (body.getExpiryDate() != null) v.setExpiryDate(body.getExpiryDate());
            if (body.getUsageLimit() != null) v.setUsageLimit(body.getUsageLimit());
            if (body.getCurrentUsage() != null) v.setCurrentUsage(body.getCurrentUsage());
            if (body.getIsActive() != null) v.setIsActive(body.getIsActive());
            return ResponseEntity.ok(voucherRepository.save(v));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/vouchers/:id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoucher(@PathVariable String id) {
        return voucherRepository.findById(id).map(v -> {
            voucherRepository.delete(v);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Đã xóa voucher"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
