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
@Document(collection = "activities")
public class Activity {

    @Id
    private String id;

    private String name;
    private String location;
    private Double price;
    private String duration;
    private String description;
    private String imageUrl;
    private String category;
    private List<String> includes;
    private String meetingPoint;
    private Map<String, Object> policies;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
