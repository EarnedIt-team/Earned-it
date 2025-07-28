package _team.earnedit.global.exception.OAuth;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;
import lombok.Getter;

@Getter
public class OAuthException extends CustomException {

  private final ErrorCode errorCode;

  public OAuthException(ErrorCode errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
  }

  public OAuthException(ErrorCode errorCode, String customMessage) {
    super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
    this.errorCode = errorCode;
  }
}
