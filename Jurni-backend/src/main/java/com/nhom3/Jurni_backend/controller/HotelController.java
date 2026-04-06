package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Hotel;
import com.nhom3.Jurni_backend.repository.HotelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelRepository hotelRepository;

    public HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    // GET /api/hotels - public, chỉ trả approved
    @GetMapping
    public ResponseEntity<?> listHotels(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sort) {
        List<Hotel> hotels = hotelRepository.findByStatusOrderByCreatedAtDesc("approved");
        hotels = filterAndSort(hotels, q, minPrice, maxPrice, sort);
        return ResponseEntity.ok(hotels.stream().map(this::formatHotel).collect(Collectors.toList()));
    }

    // GET /api/hotels/all - admin, tất cả hotels
    @GetMapping("/all")
    public ResponseEntity<?> listAllHotels(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String status) {
        List<Hotel> hotels;
        if (status != null && List.of("pending", "approved", "rejected").contains(status)) {
            hotels = hotelRepository.findByStatus(status);
        } else {
            hotels = hotelRepository.findAllByOrderByCreatedAtDesc();
        }
        hotels = filterAndSort(hotels, q, minPrice, maxPrice, sort);
        return ResponseEntity.ok(hotels.stream().map(this::formatHotel).collect(Collectors.toList()));
    }

    // GET /api/hotels/:id
    @GetMapping("/{id}")
    public ResponseEntity<?> getHotel(@PathVariable String id) {
        return hotelRepository.findById(id)
            .map(h -> ResponseEntity.ok(formatHotel(h)))
            .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/hotels
    @PostMapping
    public ResponseEntity<?> createHotel(@RequestBody Hotel hotel) {
        hotel.setStatus("pending");
        hotel.setApprovalNote(null);

        // Tính giá thấp nhất từ roomTypes nếu không có price
        if ((hotel.getPrice() == null || hotel.getPrice() == 0) && hotel.getRoomTypes() != null && !hotel.getRoomTypes().isEmpty()) {
            double minPrice = hotel.getRoomTypes().stream()
                .filter(rt -> rt.getPrice() != null && rt.getPrice() > 0)
                .mapToDouble(Hotel.RoomType::getPrice)
                .min()
                .orElse(0);
            hotel.setPrice(minPrice);
        }

        Hotel saved = hotelRepository.save(hotel);
        return ResponseEntity.status(201).body(saved);
    }

    // PUT /api/hotels/:id
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHotel(@PathVariable String id, @RequestBody Hotel body) {
        return hotelRepository.findById(id).map(hotel -> {
            // Update fields
            if (body.getName() != null) hotel.setName(body.getName());
            if (body.getLocation() != null) hotel.setLocation(body.getLocation());
            if (body.getAddress() != null) hotel.setAddress(body.getAddress());
            if (body.getPrice() != null) hotel.setPrice(body.getPrice());
            if (body.getStarRating() != null) hotel.setStarRating(body.getStarRating());
            if (body.getDescription() != null) hotel.setDescription(body.getDescription());
            if (body.getImageUrl() != null) hotel.setImageUrl(body.getImageUrl());
            if (body.getImages() != null) hotel.setImages(body.getImages());
            if (body.getCheckInTime() != null) hotel.setCheckInTime(body.getCheckInTime());
            if (body.getCheckOutTime() != null) hotel.setCheckOutTime(body.getCheckOutTime());
            if (body.getAmenities() != null) hotel.setAmenities(body.getAmenities());
            if (body.getPolicies() != null) hotel.setPolicies(body.getPolicies());
            if (body.getNearbyAttractions() != null) hotel.setNearbyAttractions(body.getNearbyAttractions());
            if (body.getPublicTransport() != null) hotel.setPublicTransport(body.getPublicTransport());
            if (body.getTotalRooms() != null) hotel.setTotalRooms(body.getTotalRooms());
            if (body.getApprovalNote() != null) hotel.setApprovalNote(body.getApprovalNote());
            if (body.getStatus() != null) hotel.setStatus(body.getStatus());

            // Update roomTypes
            if (body.getRoomTypes() != null) {
                hotel.setRoomTypes(body.getRoomTypes());
                // Tự động cập nhật lại giá thấp nhất khi sửa loại phòng
                double minPrice = body.getRoomTypes().stream()
                    .filter(rt -> rt.getPrice() != null && rt.getPrice() > 0)
                    .mapToDouble(Hotel.RoomType::getPrice)
                    .min()
                    .orElseGet(() -> Optional.ofNullable(hotel.getPrice()).orElse(0.0));
                hotel.setPrice(minPrice);
            }

            return ResponseEntity.ok(hotelRepository.save(hotel));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/hotels/:id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable String id) {
        return hotelRepository.findById(id).map(hotel -> {
            hotelRepository.delete(hotel);
            return ResponseEntity.ok(Map.of("ok", true));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Helper: filter and sort
    private List<Hotel> filterAndSort(List<Hotel> hotels, String q, Double minPrice, Double maxPrice, String sort) {
        return hotels.stream()
           .filter(h -> q == null || (h.getName() != null && h.getName().toLowerCase().contains(q.toLowerCase())))
            .filter(h -> minPrice == null || (h.getPrice() != null && h.getPrice() >= minPrice))
            .filter(h -> maxPrice == null || (h.getPrice() != null && h.getPrice() <= maxPrice))
            .sorted((a, b) -> {
                double priceA = Optional.ofNullable(a.getPrice()).orElse(0.0);
                double priceB = Optional.ofNullable(b.getPrice()).orElse(0.0);
                if ("price_asc".equals(sort)) return Double.compare(priceA, priceB);
                if ("price_desc".equals(sort)) return Double.compare(priceB, priceA);
                // Default: newest first
                if (b.getCreatedAt() != null && a.getCreatedAt() != null)
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                return 0;
            })
            .collect(Collectors.toList());
    }

    // Helper: format hotel response (matching frontend expectations)
    private Map<String, Object> formatHotel(Hotel h) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", h.getId());
        map.put("name", h.getName());
        map.put("location", h.getLocation());
        map.put("address", h.getAddress());
        map.put("price", Optional.ofNullable(h.getPrice()).orElse(0.0));
        map.put("star_rating", h.getStarRating());
        map.put("image_url", h.getImageUrl());
        map.put("images", h.getImages() != null ? h.getImages() : List.of());
        map.put("description", h.getDescription());
        map.put("amenities", h.getAmenities() != null ? h.getAmenities() : List.of());
        map.put("check_in_time", h.getCheckInTime() != null ? h.getCheckInTime() : "14:00");
        map.put("check_out_time", h.getCheckOutTime() != null ? h.getCheckOutTime() : "12:00");
        map.put("room_types", h.getRoomTypes() != null ? h.getRoomTypes() : List.of());
        int totalRooms = h.getRoomTypes() != null ? h.getRoomTypes().stream()
            .mapToInt(rt -> Optional.ofNullable(rt.getQuantity()).orElse(0)).sum() : 0;
        map.put("total_rooms", totalRooms);
        map.put("policies", h.getPolicies() != null ? h.getPolicies() : Map.of());
        map.put("nearby_attractions", h.getNearbyAttractions() != null ? h.getNearbyAttractions() : List.of());
        map.put("public_transport", h.getPublicTransport() != null ? h.getPublicTransport() : List.of());
        map.put("status", h.getStatus());
        map.put("approval_note", h.getApprovalNote());
        map.put("created_at", h.getCreatedAt());
        map.put("updated_at", h.getUpdatedAt());
        return map;
    }
}
