package com.xy.bean2json.chatgpt;

import com.xy.bean2json.chatgpt.base.AbstractChatGPT;
import com.xy.bean2json.http.ProxyAddress;

/**
 * CodeChatGPT
 *
 * @author Created by gold on 2023/4/7 09:52
 * @since 1.0.0
 */
public class CodeChatGPT extends AbstractChatGPT {

    public CodeChatGPT(String token, String model, ProxyAddress proxy) {
        super(token, model, proxy);
    }
}
