package _team.earnedit.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 예시 - ApiTestController
    EXAMPLE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "예시 예외 발생"),
    EXAMPLE_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // 회원 User
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증이 완료되지 않았습니다."),

    // 이메일 인증
    EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),

    EMAIL_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 인증 요청입니다."),
    EMAIL_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "인증 유효시간이 만료되었습니다."),
    EMAIL_TOKEN_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증이 완료된 요청입니다."),

    // 기본 예외
    UNKNOWN_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류 요청 URL을 다시 확인해보십시오."),
    ILLEGAL_STATE(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 상태입니다."),
    NULL_POINTER(HttpStatus.BAD_REQUEST, "필수 데이터가 누락되었습니다."),
    INVALID_ARGUMENT( HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 일치하지 않습니다."),
    NUMBER_FORMAT_ERROR( HttpStatus.BAD_REQUEST, "숫자 형식 오류입니다."),
    DB_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 접근 오류입니다."),
    NO_RESULT(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),
    EMPTY_RESULT(HttpStatus.NOT_FOUND, "결과가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    public String getCode() {
        return this.name();
    }
}