package _team.earnedit.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverProductSearchRequest {
    
    @Schema(description = "검색어", example = "아이폰 14", required = true)
    private String query;
    
    @Schema(description = "캐시 사용 여부", example = "true")
    @Builder.Default
    private Boolean useCache = true;
    
    @Schema(description = "이미지 배경 제거 여부 (향후 구현 예정)", example = "false")
    @Builder.Default
    private Boolean removeBackground = false;
    
    @Schema(description = "검색 결과 개수", example = "20")
    @Builder.Default
    private Integer display = 20;
    
    @Schema(description = "검색 시작 위치", example = "1")
    @Builder.Default
    private Integer start = 1;
    
    @Schema(description = "정렬 방식", example = "sim")
    @Builder.Default
    private String sort = "sim";
} 