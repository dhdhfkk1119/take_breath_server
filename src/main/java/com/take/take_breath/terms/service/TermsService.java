package com.take.take_breath.terms.service;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath.members.entity.Member;
import com.take.take_breath.members.repository.MemberRepository;
import com.take.take_breath.terms.dto.MemberTermsRequest;
import com.take.take_breath.terms.entity.MemberTerms;
import com.take.take_breath.terms.entity.Terms;
import com.take.take_breath.terms.repository.MemberTermsRepository;
import com.take.take_breath.terms.repository.TermsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final MemberRepository memberRepository;
    private final TermsRepository termsRepository;
    private final MemberTermsRepository memberTermsRepository;

    @Transactional
    public void saveMemberAgreements(Long memberId, List<MemberTermsRequest> requests) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        for (MemberTermsRequest req : requests) {
            Terms terms = termsRepository.findById(req.getTermsId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약관입니다."));

            MemberTerms memberTerms = MemberTerms.builder()
                    .member(member)
                    .terms(terms)
                    .agreed(req.isAgreed())
                    .agreedAt(req.isAgreed() ? LocalDateTime.now() : null)
                    .build();

            memberTermsRepository.save(memberTerms);
        }
    }
}