package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> {
}
