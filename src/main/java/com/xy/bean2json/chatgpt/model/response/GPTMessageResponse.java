package com.xy.bean2json.chatgpt.model.response;

import java.util.List;

/**
 * GPTMessageResponse
 *
 * @author Created by gold on 2023/4/6 17:15
 * @since 1.0.0
 */
public class GPTMessageResponse {

    /**
     * 请求id
     */
    private String id;
    /**
     * 响应类型
     */
    private String object;
    /**
     * 事件时间戳，秒
     */
    private Long created;
    /**
     * 响应模型类型
     */
    private String model;
    /**
     * 使用资源
     */
    private GPTUsage usage;
    /**
     * 返回的消息
     */
    private List<GPTChoice> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public GPTUsage getUsage() {
        return usage;
    }

    public void setUsage(GPTUsage usage) {
        this.usage = usage;
    }

    public List<GPTChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<GPTChoice> choices) {
        this.choices = choices;
    }
}
