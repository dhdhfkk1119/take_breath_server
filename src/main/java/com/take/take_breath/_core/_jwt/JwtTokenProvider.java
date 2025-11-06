package com.take.take_breath._core._jwt;

import com.take.take_breath.members.Member;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

// JJWT 0.12.x API 사용
@Slf4j
@Component // IoC 대상
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds; // Access Token 유효시간
    private final long refreshTokenValidity;   // Refresh Token 유효시간

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.validityInMilliseconds}") long validityInMilliseconds,
            @Value("${jwt.refresh-expiration-ms}") long refreshTokenValidity
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.validityInMilliseconds = validityInMilliseconds;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /**
     * Access Token 생성
     */
    public String createToken(Member member) {
        return buildToken(member, validityInMilliseconds);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Member member) {
        return buildToken(member, refreshTokenValidity);
    }

    /**
     * 토큰 공통 생성 메서드
     */
    private String buildToken(Member member, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .subject(member.getEmail())
                .claim("role", member.getRole().name())
                .claim("status", member.getStatus().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 사용자 이메일 추출
     */
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 사용자 역할 추출
     */
    public Role getRole(String token) {
        String roleStr = parseClaims(token).get("role", String.class);
        return Role.valueOf(roleStr);
    }

    /**
     * 상태 추출
     */
    public Status getStatus(String token) {
        String statusStr = parseClaims(token).get("status", String.class);
        return Status.valueOf(statusStr);
    }

    /**
     * 클레임 추출
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String regenerateAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String email = getSubject(refreshToken);
        Role role = getRole(refreshToken);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // 웹소켓 핸드셰이크 요청 토큰 추출
    public String resolveToken(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().get("Authorization");
        if (headers != null && !headers.isEmpty()) {
            String bearerToken = headers.get(0);
            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }

    // 웹소켓 channel 요청 토큰 추출
    public String resolveToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
