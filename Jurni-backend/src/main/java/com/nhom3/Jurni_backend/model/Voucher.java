package com.nhom3.Jurni_backend.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vouchers")
public class Voucher {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private String description;

    @JsonProperty("discount_percent")
    private Integer discountPercent;

    @JsonProperty("discount_amount")
    private Double discountAmount;

    @JsonProperty("min_spend")
    private Double minSpend;

    @JsonProperty("max_discount")
    private Double maxDiscount;

    @JsonProperty("start_date")
    private Instant startDate;

    @JsonProperty("expiry_date")
    private Instant expiryDate;

    @JsonProperty("usage_limit")
    private Integer usageLimit;

    @JsonProperty("current_usage")
    private Integer currentUsage = 0;

    @JsonProperty("is_active")
    private Boolean isActive = true;

    @CreatedDate
    @JsonProperty("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @JsonProperty("updated_at")
    private Instant updatedAt;
}
