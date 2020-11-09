package me.gaigeshen.doudian.api.request;

import me.gaigeshen.doudian.api.request.exception.RequestException;
import me.gaigeshen.doudian.api.request.exception.ResponseCreationException;

import java.util.List;

/**
 *
 * @author gaigeshen
 */
public interface Client {

  Response execute(Request req) throws RequestException, ResponseCreationException;

  <T extends Result> T executeResult(Request req, Class<T> resultClass) throws RequestException, ResponseCreationException;

  <T extends Result> List<T> executeResults(Request req, Class<T> resultClass) throws RequestException, ResponseCreationException;
}
