package me.gaigeshen.doudian.api.request;

import java.util.List;
import java.util.Objects;

/**
 * 此请求响应结果转换器用于转换为业务结果
 *
 * @author gaigeshen
 */
public class ResponseResultsJsonParser<T extends Result> implements ResponseParser<List<T>> {

  private final ResultJsonDeserializer deserializer = new ResultJsonDeserializerImpl();

  private final Class<T> resultItemClass;

  public ResponseResultsJsonParser(Class<T> resultItemClass) {
    this.resultItemClass = resultItemClass;
  }

  @Override
  public List<T> parse(Response resp) throws ResponseParseException, RequestResultException {
    if (Objects.isNull(resp)) {
      throw new IllegalArgumentException("Response is required");
    }
    if (resp.isFailed()) {
      throw new RequestResultException("Could not parse result from failed response, " + resp.getMessage() + "::");
    }
    try {
      return deserializer.deserializeResults(resp.getResultRawString(), resultItemClass);
    } catch (Exception e) {
      throw new ResponseParseException("Could not parse result from response:: " + resp.getResultRawString(), e);
    }
  }
}
