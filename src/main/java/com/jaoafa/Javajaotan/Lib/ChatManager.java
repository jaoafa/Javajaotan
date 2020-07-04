package com.jaoafa.Javajaotan.Lib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import net.dv8tion.jda.api.entities.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatManager {
	String docomoAPIKey = null;
	String nobyAPIKey = null; // https://www.cotogoto.ai/webapi.do
	String userlocalAPIKey = null; // http://ai.userlocal.jp
	String A3RTAPIKey = null; // https://a3rt.recruit-tech.co.jp/product/talkAPI/

	static Map<String, String> contexts = new HashMap<>();
	static Map<String, String> modes = new HashMap<>();
	static Map<String, Long> times = new HashMap<>();

	public ChatManager(String docomoAPIKey, String nobyAPIKey, String userlocalAPIKey, String A3RTAPIKey) {
		this.docomoAPIKey = docomoAPIKey;
		this.nobyAPIKey = nobyAPIKey;
		this.userlocalAPIKey = userlocalAPIKey;
		this.A3RTAPIKey = A3RTAPIKey;
	}

	public String chatDocomo(User user, String message) {
		String url = String.format(
				"https://api.apigw.smt.docomo.ne.jp/dialogue/v1/dialogue?APIKEY=%s",
				this.docomoAPIKey);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset=UTF-8");

		FormBody.Builder builder = new FormBody.Builder();
		builder.add("utt", message);

		if (times.containsKey(user.getId()) && times.get(user.getId()) > (System.currentTimeMillis() / 1000)) {
			builder.add("context", contexts.get(user.getId()));
			builder.add("mode", modes.get(user.getId()));
		}

		if (user.getName().length() <= 10) {
			builder.add("nickname", user.getName());
		}

		JSONObject json = connectPOST(headers, builder, url);
		if (json == null) {
			return null;
		}
		if (!json.has("utt")) {
			return null;
		}
		contexts.put(user.getId(), json.getString("context"));
		modes.put(user.getId(), json.getString("mode"));
		times.put(user.getId(), System.currentTimeMillis() / 1000);
		return json.getString("utt");
	}

	public String getDocomoContext(User user) {
		if (!contexts.containsKey(user.getId())) {
			return null;
		}
		return contexts.get(user.getId());
	}

	public String getDocomoMode(User user) {
		if (!modes.containsKey(user.getId())) {
			return null;
		}
		return modes.get(user.getId());
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
		String url = String.format(
				"https://chatbot-api.userlocal.jp/api/chat?key=%s&bot_name=%s&user_id=%s&user_name=%s&message=%s",
				this.docomoAPIKey,
				"jaotan",
				user.getId(),
				user.getName(),
				message);
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
}
