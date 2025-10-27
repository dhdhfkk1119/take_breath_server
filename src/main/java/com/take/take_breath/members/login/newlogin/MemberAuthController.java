package com.take.take_breath.members.login.newlogin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberAuthController {

    private final MemberAuthService memberService;

    @GetMapping("/check-id/{email}")
    public ResponseEntity<?> checkId(@PathVariable("email")String email){
        boolean isCheckId = memberService.isCheckId(email);
        return ResponseEntity.ok(isCheckId);
    }

    @PostMapping("/auth-login")
    public ResponseEntity<?> login(@Valid @RequestBody MemberRequestTo.MemberLoginRequest req) {
        MemberResponseTo.Login response = memberService.login(req);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.getAccessToken())
                .header("Refresh-Token", response.getRefreshToken())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        String newAccessToken = memberService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + newAccessToken)
                .build();
    }

}
