package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.Voucher;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface VoucherRepository extends MongoRepository<Voucher, String> {
    Optional<Voucher> findByCode(String code);
}
