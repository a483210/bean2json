package com.xy.bean2json.http;

/**
 * ProxyAddress
 *
 * @author Created by gold on 2023/4/7 09:59
 * @since 1.0.0
 */
public class ProxyAddress {

    /**
     * 地址
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;

    public ProxyAddress(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
