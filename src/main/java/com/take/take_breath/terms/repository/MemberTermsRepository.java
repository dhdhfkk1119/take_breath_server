package com.take.take_breath.terms.repository;

import com.take.take_breath.terms.entity.MemberTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermsRepository extends JpaRepository<MemberTerms, Long> {
}
