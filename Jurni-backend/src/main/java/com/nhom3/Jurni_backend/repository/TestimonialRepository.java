package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.Testimonial;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TestimonialRepository extends MongoRepository<Testimonial, String> {
    List<Testimonial> findAllByOrderByOrderAsc();
}
