package com.xy.bean2json.http;

import java.io.IOException;

/**
 * 请求参数接口
 *
 * @author Created by gold on 2023/4/6 15:00
 * @since 1.0.0
 */
public interface HttpParams {

    /**
     * 添加请求参数
     *
     * @param key   key
     * @param value value
     */
    HttpParams put(String key, Object value);

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间
     */
    void setTimeout(int timeout);

    /**
     * 发送请求
     *
     * @param baseUrl 请求地址
     */
    String send2String(String baseUrl) throws IOException;

}
