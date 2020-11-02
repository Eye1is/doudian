package me.gaigeshen.doudian.api.param;

import java.util.SortedMap;

/**
 * @author gaigeshen
 */
public interface Params extends SortedMap<String, Object> {

  String toJson();

}
