package me.gaigeshen.doudian.api.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author gaigeshen
 */
public class JacksonJsonBeanResponseHandler<T> extends BeanResponseHandler<T> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final Class<T> targetBeanClass;

  static {
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
  }

  public JacksonJsonBeanResponseHandler(Class<T> targetBeanClass) {
    this.targetBeanClass = targetBeanClass;
  }

  @Override
  protected T parseToBean(String stringEntity) {
    try {
      return objectMapper.readValue(stringEntity, targetBeanClass);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot parse to json from string entity: " + stringEntity, e);
    }
  }
}
