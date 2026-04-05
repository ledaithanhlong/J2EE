package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarRepository extends MongoRepository<Car, String> {
}
