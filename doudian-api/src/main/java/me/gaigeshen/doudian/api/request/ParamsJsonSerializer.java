package me.gaigeshen.doudian.api.request;

/**
 * @author gaigeshen
 */
public interface ParamsJsonSerializer {

  String serializer(Params params);

  String serializer(Params params, boolean stringifyValues);
}
