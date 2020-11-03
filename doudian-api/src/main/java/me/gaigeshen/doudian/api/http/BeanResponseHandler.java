package me.gaigeshen.doudian.api.http;

import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author gaigeshen
 */
public abstract class BeanResponseHandler<T> extends AbstractResponseHandler<T> {
  @Override
  public final T handleEntity(HttpEntity entity) throws IOException {
    if (entity != null) {
      return parseToBean(EntityUtils.toString(entity));
    }
    return null;
  }

  protected abstract T parseToBean(String stringEntity);

}
