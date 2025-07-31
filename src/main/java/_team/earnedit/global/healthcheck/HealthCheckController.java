package _team.earnedit.global.healthcheck;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "헬스 체크 API ", description = "서버 정상 구동 확인 API")
public class HealthCheckController {

    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "헬스 체크")
    public String healthCheck() {
        return "OK";
    }
}
