package com.xy.bean2json.chatgpt.base;

import java.util.List;

/**
 * ChatGPT
 *
 * @author Created by gold on 2023/4/6 15:14
 * @since 1.0.0
 */
public interface ChatGPT {

    /**
     * 校验token是否有效
     */
    boolean checkToken();

    /**
     * 检查模型是否可用
     *
     * @param model 模型名称
     */
    boolean checkModel(String model);

    /**
     * 发送消息
     *
     * @param prompts     提示词组
     * @param text        文本
     * @param temperature 采样温度，0-2之间，越高越随机
     * @return 结果
     */
    String completion(List<String> prompts, String text, float temperature);

}
