package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    List<Favorite> findByUserId(String userId);
    Optional<Favorite> findByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);
    void deleteByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);
}
