package com.nhom3.Jurni_backend.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flights")
public class Flight {

    @Id
    private String id;

    private String airline;

    @JsonProperty("flight_number")
    private String flightNumber;

    private String origin;
    private String destination;

    @JsonProperty("departure_city")
    private String departureCity;

    @JsonProperty("arrival_city")
    private String arrivalCity;

    @JsonProperty("departure_time")
    private Instant departureTime;

    @JsonProperty("arrival_time")
    private Instant arrivalTime;

    private Double price;

    @JsonProperty("available_seats")
    private Integer availableSeats = 0;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("flight_type")
    private String flightType; // "economy" | "business" | "first"

    private String aircraft = "Airbus A320";

    private List<String> amenities;
    private Map<String, Object> policies;

    @JsonProperty("ticket_options")
    private List<Map<String, Object>> ticketOptions;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
