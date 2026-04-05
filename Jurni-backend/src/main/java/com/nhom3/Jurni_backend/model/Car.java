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
@Document(collection = "cars")
public class Car {

    @Id
    private String id;

    private String company;
    private String model;
    private String type;
    private Double pricePerDay;
    private Integer seats;
    private String location;
    private String imageUrl;
    private Boolean available = true;
    private String description;
    private Map<String, Object> specifications;
    private List<String> amenities;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
