package me.gaigeshen.doudian.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author gaigeshen
 */
public class JsonUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private JsonUtils() { }


  public static String toJson(Object object) {
    return toJson(object, objectMapper);
  }

  public static String toJson(Object object, ObjectMapper objectMapper) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Cannot parse to json from: " + object);
    }
  }

  public static <T> T parseObject(String json, Class<T> targetClass) {
    try {
      return objectMapper.readValue(json, targetClass);
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot parse to object from: " + json);
    }
  }

  public static <T> T parseObject(String json, ObjectMapper objectMapper, Class<T> targetClass) {
    try {
      return objectMapper.readValue(json, targetClass);
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot parse to object from: " + json);
    }
  }

  public static <T> List<T> parseArray(String json) {
    try {
      return objectMapper.readValue(json, new TypeReference<List<T>>() {});
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot parse to array from: " + json);
    }
  }

  public static <T> List<T> parseArray(String json, ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(json, new TypeReference<List<T>>() {});
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot parse to array from: " + json);
    }
  }

  public static Map<String, Object> parseMapping(String json) {
    return parseMapping(parseJsonNode(json));
  }

  public static Map<String, Object> parseMapping(JsonNode jsonNode) {
    if (!jsonNode.isObject()) {
      throw new IllegalArgumentException("Can only support json object, but json input: " + jsonNode);
    }
    Map<String, Object> result = new HashMap<>();
    Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      JsonNode value = field.getValue();
      if (value.isArray()) {
        for (JsonNode internalJsonNode : value) {
          result.put(field.getKey(), parseMapping(internalJsonNode));
        }
        continue;
      }
      if (value.isValueNode()) {
        if (value.isBoolean()) {
          result.put(field.getKey(), value.booleanValue());
        } else if (value.isTextual()) {
          result.put(field.getKey(), value.textValue());
        } else if (value.isFloat()) {
          result.put(field.getKey(), value.floatValue());
        } else if (value.isDouble()) {
          result.put(field.getKey(), value.doubleValue());
        } else if (value.isInt()) {
          result.put(field.getKey(), value.intValue());
        } else if (value.isLong()) {
          result.put(field.getKey(), value.longValue());
        } else if (value.isNull()) {
          result.put(field.getKey(), null);
        }
      }
    }
    return result;
  }

  public static JsonNode parseJsonNode(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(json);
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid json input: " + json);
    }
    return jsonNode;
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

  public static JsonNode parseJsonNode(JsonNode jsonNode, String field) {
    JsonNode fieldJsonNode = jsonNode.get(field);
    if (Objects.isNull(fieldJsonNode)) {
      throw new IllegalArgumentException("Missing field, but input json is: " + jsonNode);
    }
    return fieldJsonNode;
  }

  public static boolean parseBooleanValue(JsonNode jsonNode) {
    if (!jsonNode.isBoolean()) {
      throw new IllegalArgumentException("Cannot parse to boolean from: " + jsonNode);
    }
    return jsonNode.booleanValue();
  }

  public static String parseStringValue(JsonNode jsonNode) {
    if (!jsonNode.isTextual()) {
      throw new IllegalArgumentException("Cannot parse to string from: " + jsonNode);
    }
    return jsonNode.textValue();
  }

  public static int parseIntValue(JsonNode jsonNode) {
    if (!jsonNode.canConvertToInt()) {
      throw new IllegalArgumentException("Cannot parse to int from: " + jsonNode);
    }
    return jsonNode.asInt();
  }

  public static long parseLongValue(JsonNode jsonNode) {
    if (!jsonNode.canConvertToLong()) {
      throw new IllegalArgumentException("Cannot parse to long from: " + jsonNode);
    }
    return jsonNode.asLong();
  }

  public static boolean parseBooleanValue(String json) {
    return parseBooleanValue(parseJsonNode(json));
  }

  public static String parseStringValue(String json) {
    return parseStringValue(parseJsonNode(json));
  }

  public static int parseIntValue(String json) {
    return parseIntValue(parseJsonNode(json));
  }

  public static long parseLongValue(String json) {
    return parseLongValue(parseJsonNode(json));
  }

  public static boolean parseBooleanValue(String json, String field) {
    return parseBooleanValue(parseJsonNode(json, field));
  }

  public static String parseStringValue(String json, String field) {
    return parseStringValue(parseJsonNode(json, field));
  }

  public static int parseIntValue(String json, String field) {
    return parseIntValue(parseJsonNode(json, field));
  }

  public static long parseLongValue(String json, String field) {
    return parseLongValue(parseJsonNode(json, field));
  }

  public static boolean parseBooleanValue(JsonNode jsonNode, String field) {
    return parseBooleanValue(parseJsonNode(jsonNode, field));
  }

  public static String parseStringValue(JsonNode jsonNode, String field) {
    return parseStringValue(parseJsonNode(jsonNode, field));
  }

  public static int parseIntValue(JsonNode jsonNode, String field) {
    return parseIntValue(parseJsonNode(jsonNode, field));
  }

  public static long parseLongValue(JsonNode jsonNode, String field) {
    return parseLongValue(parseJsonNode(jsonNode, field));
  }


}
