package _team.earnedit.service.socialLogin;

import _team.earnedit.dto.socialLogin.AppleUserInfoDto;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.OAuth.OAuthException;
import _team.earnedit.global.jwt.ExternalJwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AppleOAuthService {

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, String> redisTemplate;

    public AppleUserInfoDto getUserInfo(String idToken) {
        try {
            // idToken Header 에서 kid, alg 추출
            String[] tokenParts = idToken.split("\\.");
            String headerJson = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
            JsonNode header = objectMapper.readTree(headerJson);

            String kid = header.get("kid").asText();
            String alg = header.get("alg").asText();

            // 공개키 가져오기 (Redis or Apple)
            PublicKey publicKey = getApplePublicKeyFromRedisOrFetch(kid, alg);

            // idToken 파싱 + 만료 확인
            Claims claims = ExternalJwtUtil.parse(idToken, publicKey);
            if (ExternalJwtUtil.isExpired(claims)) {
                throw new OAuthException(ErrorCode.TOKEN_EXPIRED);
            }

            // 유저 정보 추출
            AppleUserInfoDto dto = new AppleUserInfoDto();
            dto.setEmail((String) claims.get("email"));
            dto.setSub(claims.getSubject());
            return dto;

        } catch (SignatureException e) {
            throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
        } catch (Exception e) {
            throw new OAuthException(ErrorCode.APPLE_ID_TOKEN_PARSING_ERROR);
        }
    }

    private PublicKey getApplePublicKeyFromRedisOrFetch(String kid, String alg) {
        try {
            String redisKey = "oauth:apple:key:" + kid + ":" + alg;

            // Redis에서 공개키 가져오기
            String encodedKey = redisTemplate.opsForValue().get(redisKey);
            if (encodedKey != null) {
                byte[] decoded = Base64.getDecoder().decode(encodedKey);
                return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            }

            // 없으면 Apple 서버에서 가져오기
            JsonNode keys = objectMapper.readTree(new URL(APPLE_PUBLIC_KEYS_URL));
            for (JsonNode key : keys.get("keys")) {
                if (key.get("kid").asText().equals(kid) && key.get("alg").asText().equals(alg)) {
                    byte[] modulusBytes = Base64.getUrlDecoder().decode(key.get("n").asText());
                    byte[] exponentBytes = Base64.getUrlDecoder().decode(key.get("e").asText());

                    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(
                            new BigInteger(1, modulusBytes),
                            new BigInteger(1, exponentBytes)
                    );
                    PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

                    // Redis 캐시 저장 (12시간)
                    redisTemplate.opsForValue().set(redisKey,
                            Base64.getEncoder().encodeToString(pubKey.getEncoded()),
                            Duration.ofHours(12));
                    return pubKey;
                }
            }

            throw new OAuthException(ErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND);
        } catch (Exception e) {
            throw new OAuthException(ErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND);
        }
    }
}
