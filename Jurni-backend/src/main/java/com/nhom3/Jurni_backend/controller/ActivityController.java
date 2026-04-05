package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Activity;
import com.nhom3.Jurni_backend.repository.ActivityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping
    public ResponseEntity<List<Activity>> listActivities() {
        return ResponseEntity.ok(activityRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getActivity(@PathVariable String id) {
        return activityRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createActivity(@RequestBody Activity activity) {
        return ResponseEntity.status(201).body(activityRepository.save(activity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable String id, @RequestBody Activity body) {
        return activityRepository.findById(id).map(act -> {
            if (body.getName() != null) act.setName(body.getName());
            if (body.getLocation() != null) act.setLocation(body.getLocation());
            if (body.getPrice() != null) act.setPrice(body.getPrice());
            if (body.getDuration() != null) act.setDuration(body.getDuration());
            if (body.getDescription() != null) act.setDescription(body.getDescription());
            if (body.getImageUrl() != null) act.setImageUrl(body.getImageUrl());
            if (body.getCategory() != null) act.setCategory(body.getCategory());
            if (body.getIncludes() != null) act.setIncludes(body.getIncludes());
            if (body.getMeetingPoint() != null) act.setMeetingPoint(body.getMeetingPoint());
            if (body.getPolicies() != null) act.setPolicies(body.getPolicies());
            return ResponseEntity.ok(activityRepository.save(act));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable String id) {
        return activityRepository.findById(id).map(act -> {
            activityRepository.delete(act);
            return ResponseEntity.ok(Map.of("ok", true));
        }).orElse(ResponseEntity.notFound().build());
    }
}
