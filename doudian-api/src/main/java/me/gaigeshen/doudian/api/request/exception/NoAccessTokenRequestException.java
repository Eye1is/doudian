package me.gaigeshen.doudian.api.request.exception;

/**
 * @author gaigeshen
 */
public class NoAccessTokenRequestException extends ShopRelatedRequestException {

  public NoAccessTokenRequestException(String shopId) {
    super(shopId);
  }

  public NoAccessTokenRequestException(String shopId, String message) {
    super(shopId, message);
  }

  public NoAccessTokenRequestException(String shopId, String message, Throwable cause) {
    super(shopId, message, cause);
  }
}
