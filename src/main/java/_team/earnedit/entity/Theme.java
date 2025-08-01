package _team.earnedit.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Theme {
    SWEET_AND_SOUR("새콤? 달콤!"),
    CS_MUST_HAVE("컴공과 필수템");

    private final String displayName;

    Theme(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
