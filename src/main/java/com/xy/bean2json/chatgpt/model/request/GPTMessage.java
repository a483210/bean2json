package com.xy.bean2json.chatgpt.model.request;

/**
 * gpt消息
 *
 * @author Created by gold on 2023/4/6 17:09
 * @since 1.0.0
 */
public class GPTMessage {

    /**
     * 角色
     */
    private String role;
    /**
     * 内容
     */
    private String content;

    public GPTMessage() {
    }

    public GPTMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
