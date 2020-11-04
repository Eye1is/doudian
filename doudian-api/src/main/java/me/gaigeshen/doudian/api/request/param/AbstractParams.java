package me.gaigeshen.doudian.api.request.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.gaigeshen.doudian.api.util.JsonUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
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
  public final String toJsonString() {
    Map<String, String> stringValueMappings = new TreeMap<>();
    for (Map.Entry<String, Object> entry : entrySet()) {
      stringValueMappings.put(entry.getKey(), entry.getValue() + "");
    }
    return JsonUtils.toJson(stringValueMappings, objectMapper);
  }

}
