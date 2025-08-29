package _team.earnedit.global.jwt;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.ErrorResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    // 5분 중복로그 방지 캐시
    private final Cache<String, Boolean> expiredTokenLogCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);
        if (token == null) {
            // Authorization이 없으면 쿠키 fallback
            token = jwtUtil.extractTokenFromCookies(request);
        }

        // 토큰 유효성 검사
        try {
            if (token != null) {
                // 1) 블랙리스트 즉시 차단
                String blKey = "BL:" + token;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(blKey))) {
                    // 토큰 원문 로그 금지 → 일부 해시만
                    log.warn("블랙리스트 토큰 접근 시도: {}...", safeTokenSuffix(token));
                    unauthorized(response, "TOKEN_BLACKLISTED", "로그아웃된 토큰입니다.");
                    return;
                }

                // 2) 접근 토큰 유효성 검증
                if (!jwtUtil.validateAccessToken(token)) {
                    unauthorized(response, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
                    return;
                }

                // 3) 클레임 파싱 & 인증 주입
                Claims claims = jwtUtil.parseClaims(token);
                Long userId = Long.valueOf(claims.getSubject());

                JwtUserInfoDto userInfo = new JwtUserInfoDto(userId);
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            if (token != null && expiredTokenLogCache.getIfPresent(token) == null) {
                log.warn("만료된 JWT 접근 감지: {}", e.getMessage());
                expiredTokenLogCache.put(token, true);
            }
            unauthorized(response, "TOKEN_EXPIRED", "만료된 토큰입니다.");
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 예외", e);
            unauthorized(response, "AUTH_ERROR", "인증 처리 중 오류가 발생했습니다.");
        }
    }

    // 엔드포인트 예외 (필터 미적용)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        return path.equals("/health")
                || path.equals("/api/auth/refresh")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.startsWith("/images/");
    }

    private void unauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        var body = ErrorResponse.fail(ErrorCode.AUTH_REQUIRED, message);

        new com.fasterxml.jackson.databind.ObjectMapper().writeValue(response.getWriter(), body);
    }

    private String safeTokenSuffix(String token) {
        return token.length() > 10 ? token.substring(token.length() - 10) : token;
    }
}
