package com.take.take_breath.social;


import com.take.take_breath.terms.dto.MemberTermsRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SocialLoginRequest {
    private String idToken;
    private String provider;
    private List<MemberTermsRequest> agreements;
}