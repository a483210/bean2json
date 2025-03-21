package com.xy.bean2json.chatgpt.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * GPTUsage
 *
 * @author Created by gold on 2023/4/6 17:18
 * @since 1.0.0
 */
public class GPTUsage {

    /**
     * 提示词使用token数量
     */
    @SerializedName("prompt_tokens")
    private Integer promptTokens;
    /**
     * 正文使用token数量
     */
    @SerializedName("completion_tokens")
    private Integer completionTokens;
    /**
     * 总使用token数量
     */
    @SerializedName("total_tokens")
    private Integer totalTokens;

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }
}
