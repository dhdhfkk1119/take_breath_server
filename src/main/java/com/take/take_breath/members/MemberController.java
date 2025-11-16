package com.take.take_breath.members;


import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.email.EmailService;
import com.take.take_breath.email.dto.EmailRequest;
import com.take.take_breath.members.dto.*;
import com.take.take_breath.members.dto.MemberRequestTo;
import com.take.take_breath.members.dto.MemberResponseTo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final MemberWithdrawalService memberWithdrawalService;


    // 일반회원 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid MemberRequest req) {
        req.setRole(Role.USER);
        memberService.signup(req);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody MemberRequestTo.MemberLoginRequest req) {
        MemberResponseTo.Login response = memberService.login(req);

        log.info("로그인 컨트롤러 : {}",response);
        // 기본 헤더 세팅
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.getAccessToken());

        // autoLogin이 true일 때만 Refresh Token 헤더 추가
        if (req.isAutoLogin()) {
            builder.header("Refresh-Token", response.getRefreshToken());
        }

        return builder.body(response);
    }


    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable("email")String email){
        log.info("아이디 체크 인증 : {}" , memberService.checkEmail(email));
        return ResponseEntity.ok(memberService.checkEmail(email));
    }

    // 이메일 찾기
    @PostMapping("/find-email")
    public ResponseEntity<?> findEmail(@RequestBody MemberFindEmailRequest req) {
        MemberEmailResponse response = memberService.findEmail(req);
        return ResponseEntity.ok(response);
    }


    // 비밀번호 인증 코드 전송
    @PostMapping("/password/reset-request")
    public ResponseEntity<String> sendResetCode(@RequestBody EmailRequest req) {
        emailService.sendVerificationCode(req); // 기존 메서드 재사용
        return ResponseEntity.ok("비밀번호 재설정용 인증 코드가 이메일로 발송되었습니다.");
    }

    // 비밀번호 재설정
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest req) {
        memberService.resetPassword(req);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    // 회원정보 불러오기
    @Auth(roles = {Role.USER}, statuses = {Status.ACTIVE})
    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfo(HttpServletRequest req) {
        String email = (String) req.getAttribute("memberEmail");
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        MemberResponse response = memberService.getMemberInfo(email);
        response.setAccessToken(token);
        return ResponseEntity.ok(response);
    }

    // 회원정보 수정
    @Auth(roles = {Role.USER}, statuses = {Status.ACTIVE})
    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(
            HttpServletRequest request,
            @RequestPart(value = "nickName", required = false) String nickName,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        String email = (String) request.getAttribute("memberEmail");
        memberService.updateMemberInfo(email, nickName, image);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // 로그아웃
    @Auth(roles = {Role.USER}, statuses = {Status.ACTIVE})
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 회원 탈퇴 요청
    @Auth(roles = {Role.USER}, statuses = {Status.ACTIVE})
    @PostMapping("/withdrawal")
    public ResponseEntity<?> requestWithdrawal(
            HttpServletRequest request,
            @RequestBody(required = false) Map<String, String> body) {

        String email = (String) request.getAttribute("memberEmail");
        String reason = (body != null && body.containsKey("reason")) ? body.get("reason") : "";

        memberWithdrawalService.requestWithdrawal(email, reason);
        return ResponseEntity.ok("탈퇴 요청이 완료되었습니다. 3개월 후 자동으로 삭제됩니다.");
    }

    // 탈퇴 취소
    @Auth(roles = {Role.USER}, statuses = {Status.WITHDRAWAL})
    @PostMapping("/cancel-withdrawal")
    public ResponseEntity<?> cancelWithdrawal(HttpServletRequest request) {
        String email = (String) request.getAttribute("memberEmail");
        memberWithdrawalService.cancelWithdrawal(email);
        return ResponseEntity.ok("계정이 복구되었습니다.");
    }

    // 토큰 새로 발급
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String newAccessToken = memberService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    // flutter 에서 FCM 토큰을 받아옴
    @PostMapping("/fcm-token")
    public ResponseEntity<Void> updateFcmToken(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> body
    ) {
        String token = authorization.replace("Bearer ", "");
        String fcmToken = body.get("fcmToken");
        memberService.updateFcmToken(token, fcmToken);
        return ResponseEntity.ok().build();
    }


}