package _team.earnedit.service.socialLogin;

import _team.earnedit.dto.socialLogin.KakaoUserInfoDto;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.OAuth.OAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoUserInfoDto getUserInfo(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfoDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
        }

    }
}