package com.jaoafa.Javajaotan.Lib;

import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.entities.User;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ChatManager {
    String nobyAPIKey; // https://www.cotogoto.ai/webapi.do
    String userlocalAPIKey; // http://ai.userlocal.jp
    String A3RTAPIKey; // https://a3rt.recruit-tech.co.jp/product/talkAPI/
    String ChaplusAPIKey; // https://k-masashi.github.io/chaplus-api-doc/

    public ChatManager(String nobyAPIKey, String userlocalAPIKey, String A3RTAPIKey, String ChaplusAPIKey) {
        this.nobyAPIKey = nobyAPIKey;
        this.userlocalAPIKey = userlocalAPIKey;
        this.A3RTAPIKey = A3RTAPIKey;
        this.ChaplusAPIKey = ChaplusAPIKey;
    }

    public String chatNoby(String message) {
        String url = String.format(
                "https://www.cotogoto.ai/webapi/noby.json?appkey=%s&text=%s",
                this.nobyAPIKey,
                message);
        JSONObject json = connect(url);
        if (json == null) {
            return null;
        }
        if (!json.has("text")) {
            return null;
        }
        return json.getString("text");
    }

    public String chatUserLocal(User user, String message) {
        String encodedName;
        String encodedMessage;
        try {
            encodedName = URLEncoder.encode(user.getName(), "UTF-8");
            encodedMessage = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        String url = String.format(
                "https://chatbot-api.userlocal.jp/api/chat?key=%s&bot_name=%s&user_id=%s&user_name=%s&message=%s",
                this.userlocalAPIKey,
                "jaotan",
                user.getId(),
                encodedName,
                encodedMessage);
        JSONObject json = connect(url);
        if (json == null) {
            return null;
        }
        if (!json.has("status")) {
            return null;
        }
        if (!json.getString("status").equalsIgnoreCase("success")) {
            return null;
        }
        return json.getString("result");
    }

    public String chatA3RT(String message) {
        String url = "https://api.a3rt.recruit-tech.co.jp/talk/v1/smalltalk";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("apikey", this.A3RTAPIKey);
        builder.add("query", message);

        JSONObject json = connectPOST(headers, builder, url);
        if (json == null) {
            return null;
        }
        if (!json.has("message")) {
            return null;
        }
        if (!json.getString("message").equalsIgnoreCase("ok")) {
            return null;
        }
        return json.getJSONArray("results").getJSONObject(0).getString("reply");
    }

    public String chatChaplus(User user, String message) {
        String url = String.format("https://www.chaplus.jp/v1/chat?apikey=%s", ChaplusAPIKey);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        JSONObject body = new JSONObject();
        body.put("utterance", message);
        body.put("username", user.getName());

        JSONObject agentState = new JSONObject();
        agentState.put("agentName", "jaotan");
        agentState.put("tone", "normal");

        body.put("agentState", agentState);

        JSONObject json = connectPOST(headers, body.toString(), url);
        if (json == null) {
            return null;
        }
        if (!json.has("bestResponse")) {
            return null;
        }
        JSONObject bestResponse = json.getJSONObject("bestResponse");
        return bestResponse.getString("utterance");
    }

    private JSONObject connect(String address) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(address).get().build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                System.out.println("[ChatManager] URLGetConnected(Error): " + address);
                System.out.println("[ChatManager] ResponseCode: " + response.code());
                if (response.body() != null) {
                    System.out.println("[ChatManager] Response: " + response.body().string());
                }
                return null;
            }
            JSONObject obj = new JSONObject(response.body().string());
            response.close();
            return obj;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject connectPOST(Map<String, String> headers, FormBody.Builder body, String address) {
        try {
            RequestBody reqbody = body.build();

            OkHttpClient client = new OkHttpClient();
            Builder builder = new Request.Builder().url(address).post(reqbody);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                System.out.println("[ChatManager] URLGetConnected(Error): " + address);
                System.out.println("[ChatManager] ResponseCode: " + response.code());
                if (response.body() != null) {
                    System.out.println("[ChatManager] Response: " + response.body().string());
                }
                return null;
            }
            JSONObject obj = new JSONObject(response.body().string());
            response.close();
            return obj;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject connectPOST(Map<String, String> headers, String json, String address) {
        try {
            final RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=UTF-8"));

            OkHttpClient client = new OkHttpClient();
            Builder builder = new Request.Builder().url(address).post(requestBody);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                System.out.println("[ChatManager] URLGetConnected(Error): " + address);
                System.out.println("[ChatManager] ResponseCode: " + response.code());
                if (response.body() != null) {
                    System.out.println("[ChatManager] Response: " + response.body().string());
                }
                return null;
            }
            JSONObject obj = new JSONObject(response.body().string());
            response.close();
            return obj;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getReplyMessage(User user, String content) {
        ChatManager chatManager = Main.getChatManager();
        if (chatManager == null) {
            return null;
        }

        if (content.startsWith("!")) {
            System.out.println("chatA3RT content: " + content.substring(1).trim());
            String ret = chatManager.chatA3RT(content.substring(1).trim());
            if (ret == null)
                return null;
            return ret + " (A3RT [③])";
        } else if (content.startsWith(":")) {
            System.out.println("chatuserLocal content: " + content.substring(1).trim());
            String ret = chatManager.chatUserLocal(user, content);
            if (ret == null)
                return null;
            return ret + " (userLocal [②])";
        } else if (content.startsWith(";")) {
            System.out.println("chatNoby content: " + content.substring(1).trim());
            String ret = chatManager.chatNoby(content.substring(1).trim());
            if (ret == null)
                return null;
            return ret + " (CotogotoNoby [④])";
        } else {
            System.out.println("content: " + content);

            String ret = chatManager.chatChaplus(user, content);
            if (ret != null) {
                return ret + " (Chaplus [①])";
            }

            ret = chatManager.chatUserLocal(user, content);
            if (ret != null) {
                return ret + " (userLocal [②])";
            }

            ret = chatManager.chatA3RT(content);
            if (ret != null) {
                return ret + " (A3RT [③])";
            }

            ret = chatManager.chatNoby(content);
            if (ret != null) {
                return ret + " (Noby [④])";
            }
        }
        return null;
    }
}
