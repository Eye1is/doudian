package me.gaigeshen.doudian.api.request.exception;

/**
 * @author gaigeshen
 */
public class ShopRelatedRequestException extends RequestException {
  private final String shopId;

  public ShopRelatedRequestException(String shopId) {
    super();
    this.shopId = shopId;
  }

  public ShopRelatedRequestException(String shopId, String message) {
    super(message);
    this.shopId = shopId;
  }

  public ShopRelatedRequestException(String shopId, String message, Throwable cause) {
    super(message, cause);
    this.shopId = shopId;
  }

  public String getShopId() {
    return shopId;
  }
}
