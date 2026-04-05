package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Testimonial;
import com.nhom3.Jurni_backend.repository.TestimonialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    private final TestimonialRepository testimonialRepository;

    public TestimonialController(TestimonialRepository testimonialRepository) {
        this.testimonialRepository = testimonialRepository;
    }

    @GetMapping
    public ResponseEntity<List<Testimonial>> listTestimonials() {
        return ResponseEntity.ok(testimonialRepository.findAllByOrderByOrderAsc());
    }

    @PostMapping
    public ResponseEntity<?> createTestimonial(@RequestBody Testimonial testimonial) {
        return ResponseEntity.status(201).body(testimonialRepository.save(testimonial));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTestimonial(@PathVariable String id, @RequestBody Testimonial body) {
        return testimonialRepository.findById(id).map(t -> {
            if (body.getName() != null) t.setName(body.getName());
            if (body.getRole() != null) t.setRole(body.getRole());
            if (body.getQuote() != null) t.setQuote(body.getQuote());
            if (body.getAvatarUrl() != null) t.setAvatarUrl(body.getAvatarUrl());
            if (body.getOrder() != null) t.setOrder(body.getOrder());
            return ResponseEntity.ok(testimonialRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestimonial(@PathVariable String id) {
        return testimonialRepository.findById(id).map(t -> {
            testimonialRepository.delete(t);
            return ResponseEntity.ok(Map.of("ok", true));
        }).orElse(ResponseEntity.notFound().build());
    }
}
