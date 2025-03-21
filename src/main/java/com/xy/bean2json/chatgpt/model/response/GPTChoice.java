package com.xy.bean2json.chatgpt.model.response;

import com.xy.bean2json.chatgpt.model.request.GPTMessage;

/**
 * GPTChoice
 *
 * @author Created by gold on 2023/4/6 17:20
 * @since 1.0.0
 */
public class GPTChoice {

    /**
     * 消息体
     */
    private GPTMessage message;
    /**
     * 消息结果
     * <p>
     * stop 完整输出
     * length 由于max_tokens导致数据不完整
     * content_filter 部分数据被过滤
     * null 还有更多数据
     */
    private String finishReason;
    /**
     * 消息索引
     */
    private Integer index;

    public GPTMessage getMessage() {
        return message;
    }

    public void setMessage(GPTMessage message) {
        this.message = message;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
