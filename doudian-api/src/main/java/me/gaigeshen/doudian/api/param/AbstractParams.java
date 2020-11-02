package me.gaigeshen.doudian.api.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.TreeMap;

/**
 * @author gaigeshen
 */
public abstract class AbstractParams extends TreeMap<String, Object> implements Params {

  private final static ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    // 只允许包含非空的字段
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // 特殊字符转义
    objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
  }

  @Override
  public final String toJson() {
    try {
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Cannot parse to json", e);
    }
  }

}
