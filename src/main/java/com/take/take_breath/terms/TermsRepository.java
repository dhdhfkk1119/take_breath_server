package com.take.take_breath.terms;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    List<Terms> findByIsRequired(boolean isRequired);
}
