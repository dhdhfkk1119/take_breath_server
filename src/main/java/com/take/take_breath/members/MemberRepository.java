package com.take.take_breath.members;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNameAndPhone(String name, String phone);
    boolean existsByEmail(String email);
    long countByStatus(Status status);
    List<Member> findByStatus(Status status);
    long countByRole(Role role);

    // 탈퇴하지않은 활성 회원만 조회
    @Query("SELECT m FROM Member m WHERE m.status IN ('ACTIVE', 'SUSPENDED', 'PENDING')")
    List<Member> findAllActiveMembers();
}
