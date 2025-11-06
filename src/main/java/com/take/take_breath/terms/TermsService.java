package com.take.take_breath.terms;

import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.terms.dto.MemberTermResponse;
import com.take.take_breath.terms.dto.MemberTermsRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<MemberTermResponse> termList() {
        List<Terms> termsList = termsRepository.findAll();
        return termsList.stream()
                .map(m -> new MemberTermResponse(m))
                .collect(Collectors.toList());
    }

    public MemberTermResponse getTerm(Long id) {
        Terms term = termsRepository.findById(id)
                .orElseThrow(() -> new Exception404("존재하지 않는 약관입니다."));
        return new MemberTermResponse(term);
    }

}