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
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증이 완료되지 않았습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 이메일이 아닙니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다."),

    // Wish
    ALREADY_EXITS_WISH(HttpStatus.CONFLICT, "이미 추가된 위시입니다."),
    WISH_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "이름은 공백값이 불가능합니다."),
    WISH_PRICE_INVALID(HttpStatus.BAD_REQUEST, "상품의 가격은 0 이상이어야 합니다."),
    WISH_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "상품 이미지는 반드시 있어야합니다."),

    WISHLIST_EMPTY(HttpStatus.NO_CONTENT, "등록된 위시가 없습니다."),
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 위시입니다."),


    // 인증 Authentication
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레쉬 토큰을 찾을 수 없습니다."),

    // 이메일 인증
    EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),

    EMAIL_TOKEN_INVALID_EMAIL(HttpStatus.BAD_REQUEST, "해당 토큰에 대한 이메일이 일치하지 않습니다."),
    EMAIL_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 인증 요청입니다."),
    EMAIL_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "인증 유효시간이 만료되었습니다."),
    EMAIL_TOKEN_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증이 완료된 요청입니다."),

    // 파일 업로드
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패하였습니다."),

    // 기본 예외
    UNKNOWN_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류 요청 URL을 다시 확인해보십시오."),
    ILLEGAL_STATE(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 상태입니다."),
    NULL_POINTER(HttpStatus.BAD_REQUEST, "필수 데이터가 누락되었습니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 일치하지 않습니다."),
    NUMBER_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "숫자 형식 오류입니다."),
    DB_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 접근 오류입니다."),
    NO_RESULT(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),
    EMPTY_RESULT(HttpStatus.NOT_FOUND, "결과가 존재하지 않습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검증 실패");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    public String getCode() {
        return this.name();
    }
}