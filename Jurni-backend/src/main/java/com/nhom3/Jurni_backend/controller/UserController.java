package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.User;
import com.nhom3.Jurni_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                return userRepository.findByClerkId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
            });
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncUser(@RequestBody Map<String, Object> body) {
        String clerkId = (String) body.get("clerkId");
        if (clerkId == null) return ResponseEntity.badRequest().body("clerkId is required");

        User user = userRepository.findByClerkId(clerkId).orElse(new User());
        user.setClerkId(clerkId);
        if (body.containsKey("email")) user.setEmail((String) body.get("email"));
        if (body.containsKey("name")) user.setName((String) body.get("name"));
        if (user.getRole() == null) user.setRole("user");

        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return userRepository.findById(id).map(user -> {
            if (body.containsKey("name") && body.get("name") != null)
                user.setName((String) body.get("name"));
            if (body.containsKey("email") && body.get("email") != null)
                user.setEmail((String) body.get("email"));
            if (body.containsKey("role") && body.get("role") != null)
                user.setRole((String) body.get("role"));
            if (body.containsKey("phone") && body.get("phone") != null)
                user.setPhone((String) body.get("phone"));
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok(Map.of("ok", true));
        }).orElse(ResponseEntity.notFound().build());
    }
}
