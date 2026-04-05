package com.nhom3.Jurni_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String userId;

    // Service references (store IDs + embedded snapshot)
    private String hotelId;
    private String flightId;
    private String carId;
    private String activityId;
    private String serviceType; // "hotel"|"flight"|"car"|"activity"

    // Snapshots for display without joins
    private Object hotel;
    private Object flight;
    private Object car;
    private Object activity;
    private Object user;
    private Object service;

    private Instant startDate;
    private Instant endDate;
    private Integer quantity = 1;
    private String itemVariant;

    private Double totalPrice;
    private String status = "pending"; // "pending"|"confirmed"|"completed"|"cancelled"|"refunded"

    // Customer info
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Payment
    private String paymentMethod;
    private String transactionId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
