package _team.earnedit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private int page;         // 현재 페이지
    private int size;         // 페이지 크기
    private long totalElements; // 전체 데이터 수
    private int totalPages;   // 전체 페이지 수
    private boolean last;     // 마지막 페이지 여부
}