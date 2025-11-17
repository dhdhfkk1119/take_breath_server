package com.take.take_breath.social;


import com.take.take_breath.terms.dto.MemberTermsRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SocialLoginRequest {
    @NotEmpty private String idToken;
    private String provider; // "google" 또는 "apple" 등
    private List<MemberTermsRequest> agreements;
}