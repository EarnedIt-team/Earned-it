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

    USER_DELETION_EXPIRED_RECOVERY(HttpStatus.FORBIDDEN, "탈퇴한 지 30일이 넘어 복구할 수 없습니다."),

    // Wish
    ALREADY_EXITS_WISH(HttpStatus.CONFLICT, "이미 추가된 위시입니다."),
    WISH_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "이름은 공백값이 불가능합니다."),
    WISH_PRICE_INVALID(HttpStatus.BAD_REQUEST, "상품의 가격은 0 이상이어야 합니다."),
    WISH_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "상품 이미지는 반드시 있어야합니다."),

    WISHLIST_EMPTY(HttpStatus.NOT_FOUND, "등록된 위시가 없습니다."),
    NOT_FOUND_SEARCH_RESULT(HttpStatus.NOT_FOUND, "검색 결과가 존재하지 않습니다."),
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 위시입니다."),
    WISH_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "다른 사용자의 위시 수정 시도입니다."),
    WISH_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "다른 사용자의 위시 삭제 시도입니다."),

    // Star
    TOP_WISH_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "Star는 5개를 초과할 수 없습니다."),
    TOP_WISH_EMPTY(HttpStatus.NOT_FOUND, "조회된 Star가 없습니다."),
    STAR_NOT_FOUND(HttpStatus.NOT_FOUND, "Star를 찾을 수 없습니다."),

    // Piece
    PIECE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 조각은 존재하지 않습니다."),
    PIECE_ALREADY_ADD(HttpStatus.NOT_FOUND, "이미 퍼즐에 추가된 조각입니다."),


    // Item
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이템은 존재하지 않습니다."),



    // Salary
    SALARY_NOT_FOUND(HttpStatus.NOT_FOUND, "조회된 급여 정보가 없습니다."),

    // profile
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    // 약관 Term
    TERM_MUST_BE_CHECKED(HttpStatus.BAD_REQUEST, "필수 약관 동의 여부는 true여야 합니다."),

    // 인증 Authentication
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레쉬 토큰을 찾을 수 없습니다."),

    // OAuth
    INVALID_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 인증 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 소셜 인증 토큰입니다."),
    APPLE_PUBLIC_KEY_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Apple 공개 키를 찾을 수 없습니다."),
    APPLE_ID_TOKEN_PARSING_ERROR(HttpStatus.UNAUTHORIZED, "Apple idToken 파싱 중 오류가 발생했습니다."),

    // 이메일 인증
    EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),

    EMAIL_TOKEN_INVALID_EMAIL(HttpStatus.BAD_REQUEST, "해당 토큰에 대한 이메일이 일치하지 않습니다."),
    EMAIL_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 인증 요청입니다."),
    EMAIL_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "인증 유효시간이 만료되었습니다."),
    EMAIL_TOKEN_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증이 완료된 요청입니다."),

    // 비밀번호 재설정
    PASSWORD_SAME_AS_BEFORE(HttpStatus.BAD_REQUEST, "이전 비밀번호와 동일합니다."),
    INVALID_LOGIN_PROVIDER(HttpStatus.BAD_REQUEST, "이메일 로그인 계정이 아닙니다."),

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