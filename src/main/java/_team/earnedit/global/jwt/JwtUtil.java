package _team.earnedit.global.jwt;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key accessKey;
    private final Key refreshKey;
    private final long accessTokenExpireTime;
    @Getter
    private final long refreshTokenExpireTime;

    // 시크릿 키 만료 시간 설정
    public JwtUtil(@Value("${jwt.secret}") String accessSecretKey,
                   @Value("${jwt.refresh_secret}") String refreshSecretKey,
                   @Value("${jwt.access_expire_time}") long accessTokenExpireTime,
                   @Value("${jwt.refresh_expire_time}") long refreshTokenExpireTime) {

        // base64가 확실하다면 아래 유지, 아니라면 getBytes(...)로 교체
        this.accessKey  = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(accessSecretKey));
        this.refreshKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(refreshSecretKey));

        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    //토큰 생성 - access, refresh 발급
    public String[] generateToken(JwtUserInfoDto user) {
        String accessToken = generateToken(user, accessKey, accessTokenExpireTime);
        String refreshToken = generateToken(user, refreshKey, refreshTokenExpireTime);
        return new String[]{accessToken, refreshToken};
    }

    // JWT 생성
    private String generateToken(JwtUserInfoDto user, Key key, long expireTime) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // access 재발급
    public String generateAccessToken(JwtUserInfoDto user) {
        return generateToken(user, accessKey, accessTokenExpireTime);
    }

    // refresh 재발급
    public String generateRefreshToken(JwtUserInfoDto user) {
        return generateToken(user, refreshKey, refreshTokenExpireTime);
    }

    // AccessToken 검증
    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    //RefreshToken 검증
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    // 토큰 검증
    private boolean validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(60) // 시계오차 허용
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.JwtException e) {
            return false;
        }
    }

    // JWT에서 Claims 추출
    // 내부용
    private Claims parseClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 외부 파싱용
    public Claims parseClaims(String token) {
        return parseClaims(token, accessKey);
    }

    // 토큰에서 userId 추출
    public String getUserIdFromToken(String token) {
        return parseClaims(token, accessKey).getSubject();
    }

    public String getUserIdFromRefreshToken(String token) {
        return parseClaims(token, refreshKey).getSubject();
    }

    // 토큰 추출
    // 요청 헤더에서 꺼낼 때
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    public String extractBearerPrefix(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }

    // 쿠키에서 access_token 꺼내기
    public String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 만료시간 확인
    public Date getAccessTokenExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

}
