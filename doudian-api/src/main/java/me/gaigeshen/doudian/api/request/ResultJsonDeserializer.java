package me.gaigeshen.doudian.api.request;

import java.util.List;

/**
 * @author gaigeshen
 */
public interface ResultJsonDeserializer {

  <T extends Result> T deserializeResult(String resultRawString, Class<T> targetClass);

  <T extends Result> List<T> deserializeResults(String resultRawString, Class<T> targetItemClass);

}
