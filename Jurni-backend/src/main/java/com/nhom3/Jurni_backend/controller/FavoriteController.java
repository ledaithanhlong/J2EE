package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Favorite;
import com.nhom3.Jurni_backend.repository.FavoriteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;

    public FavoriteController(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> listFavorites(@RequestParam(required = false) String userId) {
        if (userId != null) {
            return ResponseEntity.ok(favoriteRepository.findByUserId(userId));
        }
        return ResponseEntity.ok(favoriteRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody Favorite favorite) {
        // Check if already exists
        boolean exists = favoriteRepository.findByUserIdAndItemIdAndItemType(
            favorite.getUserId(), favorite.getItemId(), favorite.getItemType()).isPresent();
        if (exists) {
            return ResponseEntity.ok(Map.of("ok", true, "message", "Already in favorites"));
        }
        return ResponseEntity.status(201).body(favoriteRepository.save(favorite));
    }

    @DeleteMapping
    public ResponseEntity<?> removeFavorite(
            @RequestParam String userId,
            @RequestParam String itemId,
            @RequestParam String itemType) {
        favoriteRepository.deleteByUserIdAndItemIdAndItemType(userId, itemId, itemType);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
