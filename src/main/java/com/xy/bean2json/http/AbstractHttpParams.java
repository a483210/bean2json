package com.xy.bean2json.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数基类
 *
 * @author Created by gold on 2023/4/6 15:00
 * @since 1.0.0
 */
abstract class AbstractHttpParams implements HttpParams {

    protected final Map<String, Object> params = new HashMap<>();

    protected int timeout = 600 * 1000;

    @Override
    public HttpParams put(String key, Object value) {
        params.put(key, value);
        return this;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String send2String(String baseUrl) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            CloseableHttpResponse response = send(httpClient, baseUrl);
            return EntityUtils.toString(response.getEntity());
        }
    }

    protected abstract CloseableHttpResponse send(CloseableHttpClient httpClient, String baseUrl) throws IOException;

}
