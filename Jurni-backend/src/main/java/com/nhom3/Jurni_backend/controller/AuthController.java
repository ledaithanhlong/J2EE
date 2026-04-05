package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.User;
import com.nhom3.Jurni_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    @Value("${app.admin.emails:}")
    private String adminEmailsStr;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private List<String> getAdminEmails() {
        if (adminEmailsStr == null || adminEmailsStr.isBlank()) return List.of();
        return Arrays.asList(adminEmailsStr.split(","));
    }

    /**
     * POST /api/auth/sync-user
     * Frontend gửi thông tin Clerk user, backend upsert vào MongoDB
     */
    @PostMapping("/sync-user")
    public ResponseEntity<?> syncUser(@RequestBody Map<String, Object> body) {
        try {
            String clerkId = (String) body.get("clerkId");
            String email = (String) body.get("email");
            String name = (String) body.get("name");

            if (clerkId == null || clerkId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "clerkId is required"));
            }

            // Fallback for name
            if (name == null || name.isBlank()) {
                name = email != null ? email.split("@")[0] : "User";
            }

            // Fallback for email
            if (email == null || email.isBlank()) {
                email = "temp_" + clerkId + "@pending.clerk";
            }

            boolean isAdmin = getAdminEmails().contains(email);
            String role = isAdmin ? "admin" : "user";

            // Find existing user by clerkId
            Optional<User> existing = userRepository.findByClerkId(clerkId);
            User user;
            boolean created = false;

            if (existing.isPresent()) {
                user = existing.get();
                // Update if needed
                boolean hasTempEmail = user.getEmail() != null && user.getEmail().contains("@pending.clerk");
                boolean emailChanged = !email.equals(user.getEmail());
                boolean nameChanged = !name.equals(user.getName());
                boolean roleChanged = !role.equals(user.getRole());

                if (emailChanged || nameChanged || roleChanged || hasTempEmail) {
                    user.setEmail(email);
                    user.setName(name);
                    user.setRole(role);
                    userRepository.save(user);
                }
            } else {
                user = new User();
                user.setClerkId(clerkId);
                user.setEmail(email);
                user.setName(name);
                user.setRole(role);
                userRepository.save(user);
                created = true;
            }

            return ResponseEntity.ok(Map.of(
                "ok", true,
                "created", created,
                "user", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole(),
                    "clerkId", user.getClerkId()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/auth/me?clerkId=xxx
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestParam String clerkId) {
        return userRepository.findByClerkId(clerkId)
            .map(user -> ResponseEntity.ok(Map.of("user", user)))
            .orElse(ResponseEntity.status(404).build());
    }
}
