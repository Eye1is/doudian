package me.gaigeshen.doudian.api.request.sign;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * @author gaigeshen
 */
public abstract class AbstractSignGenerator implements SignGenerator {

  @Override
  public final String generate(SignParams signParams) {
    Map<String, Object> allParams = new TreeMap<>();
    allParams.put("app_key", signParams.getAppKey());
    allParams.put("timestamp", signParams.getTimestamp());
    allParams.put("v", signParams.getVersion());
    allParams.put("method", signParams.getParams().getMethod());
    allParams.put("param_json", signParams.getParams().toJsonString());

    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, Object> entry : allParams.entrySet()) {
      builder.append(entry.getKey()).append(entry.getValue());
    }

    String appSecret = signParams.getAppSecret();
    StringJoiner sj = new StringJoiner("", appSecret, appSecret);
    sj.add(builder);

    return calculate(sj.toString());
  }

  protected abstract String calculate(String algorithmInputValue);
}
