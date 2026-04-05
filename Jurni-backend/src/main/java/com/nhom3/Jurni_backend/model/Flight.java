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
@Document(collection = "flights")
public class Flight {

    @Id
    private String id;

    private String airline;
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureCity;
    private String arrivalCity;
    private Instant departureTime;
    private Instant arrivalTime;
    private Double price;
    private Integer seatsAvailable = 0;
    private Integer availableSeats = 0;
    private String imageUrl;
    private String flightType; // "economy" | "business" | "first"
    private String aircraft = "Airbus A320";

    private List<String> amenities;
    private Map<String, Object> policies;
    private List<Map<String, Object>> ticketOptions;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
