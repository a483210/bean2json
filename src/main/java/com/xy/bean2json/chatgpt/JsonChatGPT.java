package com.xy.bean2json.chatgpt;

import com.xy.bean2json.chatgpt.base.AbstractChatGPT;
import com.xy.bean2json.http.ProxyAddress;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JsonChatGPT
 *
 * @author Created by gold on 2023/4/7 09:50
 * @since 1.0.0
 */
public class JsonChatGPT extends AbstractChatGPT {

    public JsonChatGPT(String token, String model, ProxyAddress proxy) {
        super(token, model, proxy);
    }

    public String completion(String text, float temperature) {
        List<String> prompts = Arrays.asList(
                ChatGPTConstant.PROMPT_JSON,
                "要求生成的Value与Key有关，并且不能修改值原有的类型，比如Key是string，那么生成的值也必须是string"
        );
        return completion(prompts, text, temperature);
    }

    @Override
    public String completion(List<String> prompts, String text, float temperature) {
        //经过处理的prompts
        List<String> processedPrompts = prompts.stream()
                .map(JsonChatGPT::resolvePrompt)
                .collect(Collectors.toList());

        return super.completion(processedPrompts, text, temperature);
    }

    private static String resolvePrompt(String prompt) {
        Map<String, Object> values = new HashMap<>();
        values.put("language", Locale.getDefault().getDisplayLanguage());

        return new StrSubstitutor(values).replace(prompt);
    }
}
