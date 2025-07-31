package _team.earnedit.global.util;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;

import java.util.Random;

public class EmailUtils {

    private EmailUtils() {
        // 유틸 클래스 인스턴스화 방지
    }

    // 이메일 포맷 검증
    public void validateEmailFormat(String email) {
        // 1. 표준 이메일 패턴 검증
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+$";
        if (!email.matches(regex)) {
            throw new UserException(ErrorCode.INVALID_EMAIL_FORMAT);
        }

        // 2. ASCII 문자 외 금지 (한글, 특수문자 등 차단)
        if (!email.chars().allMatch(c -> c <= 127)) {
            throw new UserException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    // 5자리 숫자 랜덤 토큰 생성
    public String generateToken() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);  // 10000~99999 사이 숫자
        return String.valueOf(number);
    }
}
