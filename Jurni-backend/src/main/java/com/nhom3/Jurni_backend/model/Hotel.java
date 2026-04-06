package com.nhom3.Jurni_backend.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Double price;

    @JsonProperty("star_rating")
    private Double starRating;

    private String description;

    @JsonProperty("image_url")
    private String imageUrl;

    private List<String> images;

    @JsonProperty("check_in_time")
    private String checkInTime;

    @JsonProperty("check_out_time")
    private String checkOutTime;

    private List<String> amenities;
    private Map<String, Object> policies;

    @JsonProperty("nearby_attractions")
    private List<String> nearbyAttractions;

    @JsonProperty("public_transport")
    private List<String> publicTransport;

    // Room types embedded
    @JsonProperty("room_types")
    private List<RoomType> roomTypes;

    private String status = "pending"; // "pending" | "approved" | "rejected"

    @JsonProperty("approval_note")
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
        private List<String> images;
    }
}
