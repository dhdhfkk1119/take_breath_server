package com.take.take_breath.members;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNameAndPhone(String name, String phone);
    boolean existsByEmail(String email);
    long countByStatus(Status status);
    long countByRole(Role role);
}
