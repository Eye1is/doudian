package me.gaigeshen.doudian.api.request.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.gaigeshen.doudian.api.util.JsonUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author gaigeshen
 */
public abstract class AbstractParams extends HashMap<String, Object> implements Params {

  private final static ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    // 只允许包含非空的字段
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // 转义指定的字符
    objectMapper.getFactory().setCharacterEscapes(new SpecialCharacterEscapes());
  }

  @Override
  public final String toJsonString() {
    Map<String, String> stringValueMappings = new TreeMap<>();
    for (Map.Entry<String, Object> entry : entrySet()) {
      if (Objects.nonNull(entry.getValue())) {
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
