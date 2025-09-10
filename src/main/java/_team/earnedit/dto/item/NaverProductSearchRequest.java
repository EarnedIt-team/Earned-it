package _team.earnedit.dto.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverProductSearchRequest {
    
    @Schema(description = "검색어", example = "아이폰 14", required = true)
    @NotBlank(message = "검색어는 필수입니다.")
    private String query;
    
    @Schema(description = "캐시 사용 여부", example = "true")
    @Builder.Default
    private Boolean useCache = true;
    
    @Schema(description = "이미지 배경 제거 여부 (향후 구현 예정)", example = "false")
    @Builder.Default
    private Boolean removeBackground = false;
    
    @Schema(description = "검색 결과 개수 (1-100)", example = "20")
    @Min(value = 1, message = "검색 결과 개수는 1 이상이어야 합니다.")
    @Max(value = 100, message = "검색 결과 개수는 100 이하여야 합니다.")
    @Builder.Default
    private Integer display = 20;
    
    @JsonIgnore
    public Integer getStart() {
        return 1;
    }
    
    @JsonIgnore
    public String getSort() {
        return "sim";
    }
    
    public String getQuery() {
        return query != null ? query.trim() : null;
    }
} 