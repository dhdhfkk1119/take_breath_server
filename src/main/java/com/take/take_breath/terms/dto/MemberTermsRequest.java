package com.take.take_breath.terms.dto;

import com.take.take_breath.members.Member;
import com.take.take_breath.terms.MemberTerms;
import com.take.take_breath.terms.Terms;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberTermsRequest {
    private Long termsId; // 약관 id
    private boolean agreed;

    public MemberTerms toEntity(Member member, Terms terms) {
        return MemberTerms.builder()
                .member(member)
                .terms(terms)
                .agreed(agreed)
                .agreedAt(agreed ? LocalDateTime.now() : null)
                .build();
    }
}
