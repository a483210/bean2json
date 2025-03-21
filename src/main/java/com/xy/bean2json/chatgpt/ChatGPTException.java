package com.xy.bean2json.chatgpt;

/**
 * ChatGPTException
 *
 * @author Created by gold on 2023/4/7 17:46
 * @since 1.0.0
 */
public class ChatGPTException extends RuntimeException {

    public ChatGPTException() {
    }

    public ChatGPTException(String message) {
        super(message);
    }

    public ChatGPTException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatGPTException(Throwable cause) {
        super(cause);
    }

    public ChatGPTException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
