package me.gaigeshen.doudian.api.request.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import me.gaigeshen.doudian.api.util.JsonUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author gaigeshen
 */
public abstract class AbstractParams implements Params {

  private final static ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    objectMapper.getFactory().setCharacterEscapes(new SpecialCharacterEscapes());
  }

  @Override
  public final String toJsonString() {
    // 先按照预设的规则序列化
    String json = JsonUtils.toJson(this, objectMapper);
    // 循环所有的属性并将属性值转为字符串
    Map<String, String> stringValueMappings = new TreeMap<>();
    for (Map.Entry<String, Object> entry : JsonUtils.parseMapping(json).entrySet()) {
      if (!entry.getKey().equals("method") && Objects.nonNull(entry.getValue())) {
        stringValueMappings.put(entry.getKey(), entry.getValue() + "");
      }
    }
    return JsonUtils.toJson(stringValueMappings, objectMapper);
  }

  /**
   * @author gaigeshen
   */
  private static class SpecialCharacterEscapes extends CharacterEscapes {
    private final int[] asciiEscapes;

    public SpecialCharacterEscapes() {
      int[] esc = CharacterEscapes.standardAsciiEscapesForJSON();
      esc['<'] = CharacterEscapes.ESCAPE_STANDARD;
      esc['>'] = CharacterEscapes.ESCAPE_STANDARD;
      esc['&'] = CharacterEscapes.ESCAPE_STANDARD;
      esc['\''] = CharacterEscapes.ESCAPE_STANDARD;
      asciiEscapes = esc;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
      return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
      return null;
    }
  }

}
