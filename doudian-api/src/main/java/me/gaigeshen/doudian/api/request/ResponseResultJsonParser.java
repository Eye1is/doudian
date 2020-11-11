package me.gaigeshen.doudian.api.request;

import java.util.Objects;

/**
 * 此请求响应结果转换器用于转换为业务结果
 *
 * @author gaigeshen
 */
public class ResponseResultJsonParser<T extends Result> implements ResponseParser<T> {

  private final ResultJsonDeserializer deserializer = new ResultJsonDeserializerImpl();

  private final Class<T> resultClass;

  public ResponseResultJsonParser(Class<T> resultClass) {
    this.resultClass = resultClass;
  }

  @Override
  public T parse(Response resp) throws ResponseParseException, RequestResultException {
    if (Objects.isNull(resp)) {
      throw new IllegalArgumentException("Response is required");
    }
    if (resp.isFailed()) {
      throw new RequestResultException("Could not parse result from failed response, " + resp.getMessage() + "::");
    }
    try {
      return deserializer.deserializeResult(resp.getResultRawString(), resultClass);
    } catch (Exception e) {
      throw new ResponseParseException("Could not parse result from response:: " + resp.getResultRawString(), e);
    }
  }
}
