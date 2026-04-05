package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByClerkId(String clerkId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
