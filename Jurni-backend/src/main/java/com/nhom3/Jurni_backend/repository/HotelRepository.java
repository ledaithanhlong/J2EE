package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.Hotel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface HotelRepository extends MongoRepository<Hotel, String> {
    List<Hotel> findByStatus(String status);
    List<Hotel> findByStatusOrderByCreatedAtDesc(String status);
    List<Hotel> findAllByOrderByCreatedAtDesc();
}
