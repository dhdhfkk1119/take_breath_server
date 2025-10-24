package com.take.take_breath._core.auth;

import com.take.take_breath._core._exception.Exception401;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._exception.Exception500;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.entity.Member;
import com.take.take_breath.members.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) {
            return true;
        }

        try {
            // 1. JWT 토큰 추출 및 검증
            String token = resolveToken(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                throw new Exception401("@Auth: 유효하지 않은 토큰입니다.");
            }

            // 2. Claim에서 이메일, Role 가져오기
            String email = jwtTokenProvider.getSubject(token);
            Role role = jwtTokenProvider.getRole(token);

            // 3. DB 조회로 계정 상태 확인
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception401("@Auth: 유효하지 않은 사용자입니다."));

            // 추후 정지 넣을 경우
//            if (member.getStatus() == MemberStatus.BANNED) {
//                throw new Exception403("@Auth: 활동이 정지된 계정입니다.");
//            }

            // 4. 권한(Role) 확인
            checkRole(auth, role);

            // 5. 소유권(Owner) 확인
            checkOwnership(auth, member, request);

            // request에 JWT 정보 저장 -> 컨트롤러에서 필요 시 사용 가능
            request.setAttribute("memberEmail", email);
            request.setAttribute("memberRole", role);
            request.setAttribute("memberId", member.getId());

            return true;

        } catch (Exception401 | Exception403 e) {
            throw e;
        } catch (Exception e) {
            log.error("AuthInterceptor 처리 중 알 수 없는 오류", e);
            throw new Exception500("@Auth: 서버 처리 중 오류가 발생했습니다.");
        }
    }

    // JWT 토큰 "Bearer " 제거
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 권한(Role) 검사
    private void checkRole(Auth auth, Role userRole) {
        if (auth.roles().length > 0) {
            boolean authorized = Arrays.stream(auth.roles())
                    .anyMatch(r -> r == userRole);
            if (!authorized) {
                throw new Exception403("@Auth: 해당 리소스에 접근할 권한이 없습니다.");
            }
        }
    }

    // 소유권(Owner) 검사
    private void checkOwnership(Auth auth, Member member, HttpServletRequest request) {
        if (!auth.isOwner()) return;

        if (member.getRole() == Role.ADMIN) return; // 관리자는 통과

        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String idStr = pathVariables.get("id");
        if (idStr == null) {
            log.warn("@Auth(isOwner=true): URL에 {id} 변수가 없음");
            throw new Exception500("@Auth: 소유자 확인 설정 오류");
        }

        try {
            Long resourceId = Long.parseLong(idStr);
            if (!Objects.equals(member.getId(), resourceId)) {
                throw new Exception403("@Auth: 자신의 리소스에만 접근 가능합니다.");
            }
        } catch (NumberFormatException e) {
            log.warn("@Auth(isOwner=true): URL {id} 값이 숫자가 아님 ({})", idStr);
            throw new Exception403("@Auth: 잘못된 요청입니다.");
        }
    }
}
