package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Voucher;
import com.nhom3.Jurni_backend.repository.VoucherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherRepository voucherRepository;

    public VoucherController(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @GetMapping
    public ResponseEntity<List<Voucher>> listVouchers() {
        return ResponseEntity.ok(voucherRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucher(@PathVariable String id) {
        return voucherRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> findByCode(@PathVariable String code) {
        return voucherRepository.findByCode(code)
            .map(v -> ResponseEntity.ok((Object) v))
            .orElse(ResponseEntity.status(404).body(Map.of("error", "Voucher not found")));
    }

    @PostMapping
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {
        // Validation
        if (voucher.getCode() == null || voucher.getCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mã voucher không được để trống"));
        }
        if ((voucher.getDiscountPercent() == null || voucher.getDiscountPercent() <= 0) &&
            (voucher.getDiscountAmount() == null || voucher.getDiscountAmount() <= 0)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chiết khấu phải > 0"));
        }
        if (voucher.getExpiryDate() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ngày hết hạn không được để trống"));
        }
        if (voucher.getCurrentUsage() == null) {
            voucher.setCurrentUsage(0);
        }
        return ResponseEntity.status(201).body(voucherRepository.save(voucher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoucher(@PathVariable String id, @RequestBody Voucher body) {
        return voucherRepository.findById(id).map(v -> {
            if (body.getCode() != null && !body.getCode().trim().isEmpty()) v.setCode(body.getCode());
            if (body.getDiscountPercent() != null) v.setDiscountPercent(body.getDiscountPercent());
            if (body.getDiscountAmount() != null) v.setDiscountAmount(body.getDiscountAmount());
            if (body.getMinSpend() != null) v.setMinSpend(body.getMinSpend());
            if (body.getMaxDiscount() != null) v.setMaxDiscount(body.getMaxDiscount());
            if (body.getStartDate() != null) v.setStartDate(body.getStartDate());
            if (body.getExpiryDate() != null) v.setExpiryDate(body.getExpiryDate());
            if (body.getUsageLimit() != null) v.setUsageLimit(body.getUsageLimit());
            if (body.getCurrentUsage() != null) v.setCurrentUsage(body.getCurrentUsage());
            return ResponseEntity.ok(voucherRepository.save(v));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoucher(@PathVariable String id) {
        return voucherRepository.findById(id).map(v -> {
            voucherRepository.delete(v);
            return ResponseEntity.ok(Map.of("ok", true));
        }).orElse(ResponseEntity.notFound().build());
    }
}
