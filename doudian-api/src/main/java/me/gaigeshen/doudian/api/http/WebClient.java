package me.gaigeshen.doudian.api.http;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author gaigeshen
 */
public class WebClient implements Closeable {

  private final CloseableHttpClient client;

  public static Builder builder() {
    return Builder.create();
  }

  private WebClient(int connectionRequestTimeout, int connectTimeout, int socketTimeout, SSLContext sslContext) {
    RequestConfig config = RequestConfig.custom()
            .setConnectionRequestTimeout(connectionRequestTimeout)
            .setConnectTimeout(connectTimeout)
            .setSocketTimeout(socketTimeout)
            .build();

    SSLConnectionSocketFactory sslConnSocFactory = sslContext != null
            ? new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)
            : SSLConnectionSocketFactory.getSocketFactory();

    Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", sslConnSocFactory)
            .build();

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(sfr);
    connectionManager.setDefaultMaxPerRoute(20); // Set the maximum number of concurrent connections per route, which is 2 by default
    connectionManager.setMaxTotal(200); // Set the maximum number of total open connections
    connectionManager.setValidateAfterInactivity(1000);

    this.client = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setKeepAliveStrategy(new KeepAliveStrategy())
            .evictExpiredConnections()
            .evictIdleConnections(1800, TimeUnit.SECONDS)
            .setDefaultRequestConfig(config)
            .build();
  }

  public String execute(HttpUriRequest req) throws WebClientException {
    return execute(req, new BasicResponseHandler());
  }

  public <T> T execute(HttpUriRequest req, AbstractResponseHandler<T> handler) throws WebClientException {
    try {
      return client.execute(req, handler);
    } catch (IOException e) {
      throw new WebClientException("Could not execute request:: " + req, e);
    }
  }

  @Override
  public void close() throws IOException {
    client.close();
  }

  /**
   * @author gaigeshen
   */
  public static class Builder {
    private int connectionRequestTimeout = 1000;
    private int connectTimeout = 2000;
    private int socketTimeout = 5000;
    private SSLContext sslContext = null;

    public static Builder create() {
      return new Builder();
    }

    public Builder setConnectionRequestTimeout(int connectionRequestTimeout) {
      this.connectionRequestTimeout = Args.positive(connectionRequestTimeout, "connectionRequestTimeout");
      return this;
    }

    public Builder setConnectTimeout(int connectTimeout) {
      this.connectTimeout = Args.positive(connectTimeout, "connectTimeout");
      return this;
    }

    public Builder setSocketTimeout(int socketTimeout) {
      this.socketTimeout = Args.positive(socketTimeout, "socketTimeout");
      return this;
    }

    public Builder setSslContext(SSLContext sslContext) {
      this.sslContext = sslContext;
      return this;
    }

    public WebClient build() {
      return new WebClient(connectionRequestTimeout, connectTimeout, socketTimeout, sslContext);
    }
  }

  /**
   * @author gaigeshen
   */
  private static class KeepAliveStrategy implements ConnectionKeepAliveStrategy {
    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
      HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
      while (it.hasNext()) {
        HeaderElement headerElement = it.nextElement();
        String name = headerElement.getName();
        String value = headerElement.getValue();
        if (value != null && name.equalsIgnoreCase("timeout")) {
          try {
            return Long.parseLong(value) * 1000;
          } catch (NumberFormatException ignored) {
          }
        }
      }
      return 65 * 1000;
    }
  }
}
