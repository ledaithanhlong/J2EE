package com.nhom3.Jurni_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hotels")
public class Hotel {

    @Id
    private String id;

    private String name;
    private String location;
    private String address;
    private Double price = 0.0;
    private Double starRating;
    private String description;
    private String imageUrl;
    private List<String> images;
    private String checkInTime = "14:00";
    private String checkOutTime = "12:00";
    private Integer totalFloors;

    private List<String> amenities;
    private Map<String, Object> policies;
    private List<Map<String, Object>> nearbyAttractions;
    private List<Map<String, Object>> publicTransport;

    // Room types embedded (thay cho bảng Rooms riêng)
    private List<RoomType> roomTypes;

    private Boolean hasBreakfast = false;
    private Boolean hasParking = false;
    private Boolean hasWifi = true;
    private Boolean hasPool = false;
    private Boolean hasRestaurant = false;
    private Boolean hasGym = false;
    private Boolean hasSpa = false;
    private Boolean allowsPets = false;
    private Boolean isSmokingAllowed = false;

    private String status = "pending"; // "pending" | "approved" | "rejected"
    private String approvedBy;
    private Instant approvedAt;
    private String approvalNote;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomType {
        private String type;
        private Integer quantity;
        private Double price;
        private Integer capacity;
    }
}
