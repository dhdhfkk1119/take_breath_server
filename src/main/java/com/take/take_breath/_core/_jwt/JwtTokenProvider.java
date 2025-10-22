package com.take.take_breath._core._jwt;

import com.take.take_breath.members.Role;
import com.take.take_breath.members.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// JJWT 0.12.x API 사용
@Slf4j
@Component // IoC 대상
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    // 생성자 주입으로 설계해 보자 ( 주입 시 연산을 해야될 경우 직접 생성자를 만들어서 셋팅)
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expiration-in-ms}") long validityInMilliseconds) {
        // 1. 주입받은 비밀 키 문자열을 Base64 값을 디코딩 하여 byte 배열로 변환 합니다.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // 2. 알고리즘을 사용할 SecretKey 객체를 생성 한다.
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(Member member) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        // 속성 조사해 보기
        return Jwts.builder()
                .subject(member.getEmail())
                .claim("role", member.getRole().name())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰의 전체 유효성 검증
     * @param token 검증할 JWT
     * @return 유효하면 true 반환, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명 입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다");
        } catch (Exception e) {
            log.error("JWT 토큰이 잘못되었습니다");
        }
        return false;
    }

    /**
     * 전체 토큰에서 사용자 이메일(Subject)을 추출 합니다.
     * @return 추출된 사용자 이메일(String)
     */
    public String getSubject(String token) {

        return parseClaims(token).getSubject();
    }

    /**
     * 전체 토큰에서 사용자 role(claim)을 추출 합니다.
     * @return 추출된 사용자 역할(claim)
     */
    public Role getRole(String token) {
        String roleStr = parseClaims(token).get("role", String.class);
        return Role.valueOf(roleStr);
    }

    /**
     * 클레임 정보를 추출하는 기능
     */
    private Claims parseClaims(String token) {
        try {
            return  Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었더라도, 만료 정보를 확인하기 위해 클레임 자체를 반환해 줍니다.
            return e.getClaims();
        }

    }

}
