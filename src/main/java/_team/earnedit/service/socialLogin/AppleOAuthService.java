package _team.earnedit.service.socialLogin;

import _team.earnedit.dto.socialLogin.AppleUserInfoDto;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.OAuth.OAuthException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AppleOAuthService {

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AppleUserInfoDto getUserInfo(String idToken) {
        try {
            // idToken Header 에서 kid, alg 추출
            String[] tokenParts = idToken.split("\\.");
            String headerJson = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
            JsonNode header = objectMapper.readTree(headerJson);

            String kid = header.get("kid").asText();
            String alg = header.get("alg").asText();

            // Apple 공개 키 목록 불러와 매칭되는 키 찾기
            JsonNode publicKeys = objectMapper.readTree(new URL(APPLE_PUBLIC_KEYS_URL));

            JsonNode matchedKey = null;
            for (JsonNode key : publicKeys.get("keys")) {
                if (key.get("kid").asText().equals(kid) && key.get("alg").asText().equals(alg)) {
                    matchedKey = key;
                    break;
                }
            }

            if (matchedKey == null) {
                throw new OAuthException(ErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND);
            }

            // 공개 키 생성
            byte[] modulusBytes = Base64.getUrlDecoder().decode(matchedKey.get("n").asText());
            byte[] exponentBytes = Base64.getUrlDecoder().decode(matchedKey.get("e").asText());

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(
                    new BigInteger(1, modulusBytes),
                    new BigInteger(1, exponentBytes)
            );
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

            // idToken 검증 + 파싱
            Claims claims = Jwts.parserBuilder()
                    .deserializeJsonWith(new JacksonDeserializer<>())
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();

            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                throw new OAuthException(ErrorCode.TOKEN_EXPIRED);
            }

            String email = (String) claims.get("email");
            String sub = claims.getSubject();

            AppleUserInfoDto dto = new AppleUserInfoDto();
            dto.setEmail(email);
            dto.setSub(sub);
            return dto;

        } catch (SignatureException e) {
            throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
        } catch (Exception e) {
            throw new OAuthException(ErrorCode.APPLE_ID_TOKEN_PARSING_ERROR);
        }
    }
}
