package me.gaigeshen.doudian.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import me.gaigeshen.doudian.api.util.JsonUtils;

import java.util.Map;

/**
 * 默认的响应结果
 *
 * @author gaigeshen
 */
public class DefaultResponse implements Response {

  private final String rawString;

  private final String resultRawString;

  private final JsonNode resultJsonNode;

  private final boolean success;

  private final String message;

  private DefaultResponse(String rawString, JsonNode resultJsonNode, boolean success, String message) {
    this.rawString = rawString;
    this.resultRawString = JsonUtils.toJson(resultJsonNode);
    this.resultJsonNode = resultJsonNode;
    this.success = success;
    this.message = message;
  }

  public static DefaultResponse create(String rawString) throws ResponseParseException {
    try {
      JsonNode jsonNode = parseJsonNode(rawString);
      JsonNode reusltJsonNode = parseResultJsonNode(jsonNode);
      return new DefaultResponse(rawString, reusltJsonNode, confirmSuccessStatus(jsonNode), confirmMessageStatus(jsonNode));
    } catch (Exception e) {
      throw new ResponseParseException("Cannot create response:: raw string " + rawString, e);
    }
  }

  private static JsonNode parseJsonNode(String rawString) {
    return JsonUtils.parseJsonNode(rawString);
  }

  private static JsonNode parseResultJsonNode(JsonNode rawJsonNode) {
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
  public String getResultRawString() {
    return resultRawString;
  }

  @Override
  public boolean isFailed() {
    return !success;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Map<String, Object> parseMapping() throws ResponseParseException {
    try {
      return JsonUtils.parseMapping(resultJsonNode);
    } catch (Exception e) {
      throw new ResponseParseException("Cannot parse to mapping:: " + resultRawString);
    }
  }
}
