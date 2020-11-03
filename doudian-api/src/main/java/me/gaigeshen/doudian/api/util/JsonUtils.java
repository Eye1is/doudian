package me.gaigeshen.doudian.api.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author gaigeshen
 */
public class JsonUtils {

  private final static ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  private JsonUtils() { }

  public static String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Cannot to json from: " + object);
    }
  }

  public static JsonNode parseJsonNode(String json, String field) {
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(json);
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid json input: " + json);
    }
    if (Objects.isNull(jsonNode) || !jsonNode.isObject()) {
      throw new IllegalArgumentException("Can only support json object, but input json is: " + json);
    }
    if (StringUtils.isNotBlank(field)) {
      jsonNode = jsonNode.get(field);
    }
    if (Objects.isNull(jsonNode)) {
      throw new IllegalArgumentException("Missing field, but input json is: " + json);
    }
    return jsonNode;
  }

  public static String parseStringValue(JsonNode jsonNode) {
    if (!jsonNode.isTextual()) {
      throw new IllegalArgumentException("Cannot parse to string from: " + jsonNode);
    }
    return jsonNode.asText();
  }

  public static int parseIntValue(JsonNode jsonNode) {
    if (!jsonNode.isInt()) {
      throw new IllegalArgumentException("Cannot parse to int from: " + jsonNode);
    }
    return jsonNode.asInt();
  }

  public static <T> T parseObject(JsonNode jsonNode, Class<T> targetClass) {
    if (!jsonNode.isObject()) {
      throw new IllegalArgumentException("Can only support json object, but json input: " + jsonNode);
    }
    try {
      return objectMapper.treeToValue(jsonNode, targetClass);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Cannot parse to object from: " + jsonNode);
    }
  }

  public static <T> List<T> parseArray(JsonNode jsonNode, Class<T> targetClass) {
    if (!jsonNode.isArray()) {
      throw new IllegalArgumentException("Can only support json array by field, but json input: " + jsonNode);
    }
    List<T> result = new ArrayList<>();
    try {
      for (JsonNode node : jsonNode) {
        result.add(objectMapper.treeToValue(node, targetClass));
      }
      return result;
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Cannot parse to array from: " + jsonNode);
    }
  }

  public static String parseStringValue(String json, String field) {
    return parseStringValue(parseJsonNode(json, field));
  }

  public static int parseIntValue(String json, String field) {
    return parseIntValue(parseJsonNode(json, field));
  }

  public static <T> T parseObject(String json, Class<T> targetClass, String field) {
    return parseObject(parseJsonNode(json, field), targetClass);
  }

  public static <T> List<T> parseArray(String json, Class<T> targetClass, String field) {
    return parseArray(parseJsonNode(json, field), targetClass);
  }
}
