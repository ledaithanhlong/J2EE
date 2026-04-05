package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.TeamMember;
import com.nhom3.Jurni_backend.repository.TeamMemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamMemberRepository teamMemberRepository;

    public TeamController(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    @GetMapping
    public ResponseEntity<List<TeamMember>> listTeam() {
        return ResponseEntity.ok(teamMemberRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createTeamMember(@RequestBody TeamMember member) {
        return ResponseEntity.status(201).body(teamMemberRepository.save(member));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeamMember(@PathVariable String id, @RequestBody TeamMember body) {
        return teamMemberRepository.findById(id).map(m -> {
            if (body.getName() != null) m.setName(body.getName());
            if (body.getRole() != null) m.setRole(body.getRole());
            if (body.getBio() != null) m.setBio(body.getBio());
            if (body.getInitials() != null) m.setInitials(body.getInitials());
            if (body.getImageUrl() != null) m.setImageUrl(body.getImageUrl());
            if (body.getColor() != null) m.setColor(body.getColor());
            if (body.getGroup() != null) m.setGroup(body.getGroup());
            return ResponseEntity.ok(teamMemberRepository.save(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeamMember(@PathVariable String id) {
        return teamMemberRepository.findById(id).map(m -> {
            teamMemberRepository.delete(m);
            return ResponseEntity.ok(Map.of("ok", true));
        }).orElse(ResponseEntity.notFound().build());
    }
}
