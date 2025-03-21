package com.xy.bean2json.chatgpt.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * gpt模型类型
 *
 * @author Created by gold on 2023/4/6 15:28
 * @since 1.0.0
 */
public class GPTModel {

    /**
     * 模型名称
     */
    private String id;
    /**
     * 类型
     */
    private String object;
    /**
     * 所有者
     */
    @SerializedName("owned_by")
    private String ownedBy;
    /**
     * 权限
     */
    private Map<String, String> permission;

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

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public Map<String, String> getPermission() {
        return permission;
    }

    public void setPermission(Map<String, String> permission) {
        this.permission = permission;
    }
}



