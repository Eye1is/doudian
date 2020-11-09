package me.gaigeshen.doudian.api.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import me.gaigeshen.doudian.api.util.JsonUtils;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author gaigeshen
 */
public class ResultJsonDeserializerImpl implements ResultJsonDeserializer {

  private final static ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    // 驼峰转下划线
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
  }

  @Override
  public <T extends Result> T deserializeResult(String resultRawString, Class<T> targetClass) {
    return JsonUtils.parseObject(resultRawString, targetClass);
  }

  @Override
  public <T extends Result> List<T> deserializeResults(String resultRawString, Class<T> targetItemClass) {
    return JsonUtils.parseArray(resultRawString, targetItemClass);
  }
}
