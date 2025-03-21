package com.xy.bean2json.http;

import com.xy.bean2json.utils.JsonUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

/**
 * post请求
 *
 * @author Created by gold on 2023/4/6 15:00
 * @since 1.0.0
 */
public class HttpPostParams extends AbstractHttpParams {

    private final String token;
    private final ProxyAddress proxy;

    public HttpPostParams(String token, ProxyAddress proxy) {
        this.token = token;
        this.proxy = proxy;
    }

    @Override
    protected CloseableHttpResponse send(CloseableHttpClient httpClient, String baseUrl) throws IOException {
        HttpPost request = new HttpPost(baseUrl);

        RequestConfig.Builder localConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout);

        if (proxy != null) {
            HttpHost httpHost = new HttpHost(proxy.getHost(), proxy.getPort());

            localConfig.setProxy(httpHost);
        }

        request.setConfig(localConfig.build());

        request.setHeader("Authorization", "Bearer " + token);

        StringEntity requestEntity = new StringEntity(JsonUtils.toJson(params), ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        return httpClient.execute(request);
    }
}
