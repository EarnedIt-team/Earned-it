package _team.earnedit.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;

import java.security.PublicKey;
import java.util.Date;

public class ExternalJwtUtil {

    // 공개키 기반 JWT 파싱
    public static Claims parse(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .deserializeJsonWith(new JacksonDeserializer<>())
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 만료 여부 확인
    public static boolean isExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }
}
