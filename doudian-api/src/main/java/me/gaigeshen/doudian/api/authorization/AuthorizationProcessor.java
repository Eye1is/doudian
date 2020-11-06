package me.gaigeshen.doudian.api.authorization;

/**
 * @author gaigeshen
 */
public interface AuthorizationProcessor {

  String getAuthorizeUri(String state);

  String getAuthorizeUri();

  AccessToken handleAuthorized(String authorizationCode);
}
