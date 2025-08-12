package _team.earnedit.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Theme {
    SWEET_AND_SOUR("새콤? 달콤!"),
    CS_MUST_HAVE("컴공과 필수템"),
    SMALL_HAPPINESS_INTERIOR("소확행 인테리어"),
    SENSUAL_CHOCOLATE("감성 초콜릿 컬렉션"),
    NOSTALGIA_PACKAGE("추억의 아이템"),
    DAILY_OFFICE_WORKER("회사원 일상"),
    TIMID_LUXURY("소심한 사치");

    private final String displayName;

    Theme(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
