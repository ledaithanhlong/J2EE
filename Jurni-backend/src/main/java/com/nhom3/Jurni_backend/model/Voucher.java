package com.nhom3.Jurni_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vouchers")
public class Voucher {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private Integer discountPercent;
    private Double discountAmount;
    private Double minSpend;
    private Double maxDiscount;
    private Instant startDate;
    private Instant expiryDate;
    private Integer usageLimit;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
