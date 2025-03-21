package com.xy.bean2json.chatgpt;

/**
 * ChatGPTConstant
 *
 * @author Created by gold on 2023/4/6 15:29
 * @since 1.0.0
 */
public interface ChatGPTConstant {

    /**
     * 主接口
     */
    String URL_MAIN = "https://api.openai.com/v1/chat/completions";

    /**
     * 获取可用的模型
     */
    String URL_MODEL_LIST = "https://api.openai.com/v1/models";

    /**
     * 提示词json
     */
    String PROMPT_JSON = "使用${language}随机更新Json的值，并且去除所有的额外字符后返回Json";
    /**
     * 提示词代码
     */
    String PROMPT_CODE = "";

}
