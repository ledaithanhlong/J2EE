package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.TeamMember;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamMemberRepository extends MongoRepository<TeamMember, String> {
}
