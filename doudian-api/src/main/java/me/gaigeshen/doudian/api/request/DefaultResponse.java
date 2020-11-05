package me.gaigeshen.doudian.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import me.gaigeshen.doudian.api.util.JsonUtils;

import java.util.List;
import java.util.Map;

/**
 * 默认的响应结果
 *
 * @author gaigeshen
 */
public class DefaultResponse implements Response {

  private final String rawString;

  private final boolean success;

  private final String message;

  private final JsonNode dataJsonNode;

  private DefaultResponse(String rawString, boolean success, String message, JsonNode dataJsonNode) {
    this.rawString = rawString;
    this.success = success;
    this.message = message;
    this.dataJsonNode = dataJsonNode;
  }

  public static DefaultResponse create(String rawString) {
    JsonNode jsonNode = parseJsonNode(rawString);
    JsonNode dataJsonNode = parseDataJsonNode(jsonNode);
    return new DefaultResponse(rawString, confirmSuccessStatus(jsonNode), confirmMessageStatus(jsonNode), dataJsonNode);
  }

  private static JsonNode parseJsonNode(String rawString) {
    return JsonUtils.parseJsonNode(rawString);
  }

  private static JsonNode parseDataJsonNode(JsonNode rawJsonNode) {
    return JsonUtils.parseJsonNode(rawJsonNode, "data");
  }

  private static boolean confirmSuccessStatus(JsonNode rawJsonNode) {
    return JsonUtils.parseIntValue(rawJsonNode, "err_no") == 0;
  }

  private static String confirmMessageStatus(JsonNode rawJsonNode) {
    return JsonUtils.parseStringValue(rawJsonNode, "message");
  }

  @Override
  public String getRawString() {
    return rawString;
  }

  @Override
  public boolean isSuccess() {
    return success;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Map<String, Object> parseMapping() {
    return JsonUtils.parseMapping(dataJsonNode);
  }

  @Override
  public <T> T parseObject(Class<T> targetClass) {
    return JsonUtils.parseObject(dataJsonNode, targetClass);
  }

  @Override
  public <T> List<T> parseList(Class<T> itemClass) {
    return JsonUtils.parseArray(dataJsonNode, itemClass);
  }

}
