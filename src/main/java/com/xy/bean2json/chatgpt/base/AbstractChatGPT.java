package com.xy.bean2json.chatgpt.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xy.bean2json.chatgpt.ChatGPTConstant;
import com.xy.bean2json.chatgpt.ChatGPTException;
import com.xy.bean2json.chatgpt.model.request.GPTMessage;
import com.xy.bean2json.chatgpt.model.response.GPTChoice;
import com.xy.bean2json.chatgpt.model.response.GPTMessageResponse;
import com.xy.bean2json.http.HttpGetParams;
import com.xy.bean2json.http.HttpPostParams;
import com.xy.bean2json.http.ProxyAddress;
import com.xy.bean2json.utils.JsonUtils;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * AbstractChatGPT
 *
 * @author Created by gold on 2023/4/6 15:19
 * @since 1.0.0
 */
public class AbstractChatGPT implements ChatGPT {

    private final String token;
    private final String model;
    private final ProxyAddress proxy;

    public AbstractChatGPT(String token, String model, ProxyAddress proxy) {
        this.token = token;
        this.model = model;
        this.proxy = proxy;
    }

    @Override
    public boolean checkToken() {
        HttpPostParams params = new HttpPostParams(token, proxy);

        GPTMessage[] messages = new GPTMessage[]{new GPTMessage("user", "Say this is a test")};

        params.put("model", model);
        params.put("messages", JsonUtils.toJson(messages));

        try {
            String response = params.send2String(ChatGPTConstant.URL_MAIN);

            return checkPass(response);
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    @Override
    public boolean checkModel(String model) {
        HttpGetParams params = new HttpGetParams(token, proxy);

        try {
            String response = params.send2String(ChatGPTConstant.URL_MODEL_LIST + "/" + model);

            return checkPass(response);
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    private static boolean checkPass(String response) {
        JsonElement element = JsonParser.parseString(response);
        if (!element.isJsonObject()) {
            return false;
        }

        JsonObject object = element.getAsJsonObject();

        return object.has("id") && !object.has("error");
    }

    @Override
    public String completion(List<String> prompts, String text, float temperature) {
        HttpPostParams params = new HttpPostParams(token, proxy);

        GPTMessage[] messages = new GPTMessage[prompts.size() + 1];
        for (int i = 0; i < prompts.size(); i++) {
            messages[i] = new GPTMessage("system", prompts.get(i));
        }
        messages[prompts.size()] = new GPTMessage("user", text);

        if (temperature <= 0.1) {
            temperature = 0.1f;
        } else if (temperature >= 2) {
            temperature = 2f;
        }

        params.put("model", model);
        params.put("messages", messages);
        params.put("temperature", temperature);

        try {
            String response = params.send2String(ChatGPTConstant.URL_MAIN);

            JsonElement element = JsonParser.parseString(response);
            if (!element.isJsonObject()) {
                return "error";
            }

            JsonObject object = element.getAsJsonObject();
            if (object.has("error")) {
                JsonElement errorElement = object.get("error");
                if (errorElement.isJsonPrimitive()) {
                    return "error " + errorElement.getAsString();
                }

                return "error " + errorElement.getAsJsonObject().get("message").getAsString();
            }

            GPTMessageResponse messageResponse = JsonUtils.GSON.fromJson(object, GPTMessageResponse.class);

            List<GPTChoice> choices = messageResponse.getChoices();
            if (CollectionUtils.isEmpty(choices)) {
                return null;
            }

            return choices.get(0).getMessage().getContent();
        } catch (IOException e) {
            throw new ChatGPTException(e);
        }
    }
}
