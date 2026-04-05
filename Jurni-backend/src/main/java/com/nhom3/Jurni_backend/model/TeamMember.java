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
@Document(collection = "team_members")
public class TeamMember {

    @Id
    private String id;

    private String name;
    private String role;
    private String bio;
    private String initials;
    private String imageUrl;
    private String color = "bg-blue-500";
    private String group = "staff";

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
