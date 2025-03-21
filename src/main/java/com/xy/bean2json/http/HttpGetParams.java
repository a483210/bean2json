package com.xy.bean2json.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.Map;

/**
 * get请求
 *
 * @author Created by gold on 2023/4/6 17:05
 * @since 1.0.0
 */
public class HttpGetParams extends AbstractHttpParams {

    private final String token;
    private final ProxyAddress proxy;

    public HttpGetParams(String token, ProxyAddress proxy) {
        this.token = token;
        this.proxy = proxy;
    }

    @Override
    protected CloseableHttpResponse send(CloseableHttpClient httpClient, String baseUrl) throws IOException {
        String params = resolveParams();
        if (StringUtils.isNotBlank(params)) {
            baseUrl += "?" + params;
        }

        HttpGet request = new HttpGet(baseUrl);

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

        return httpClient.execute(request);
    }

    private String resolveParams() {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());

            if (i < params.size() - 1) {
                builder.append("&");
            }

            i++;
        }

        return builder.toString();
    }
}
