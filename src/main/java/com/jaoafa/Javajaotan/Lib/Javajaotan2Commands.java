package com.jaoafa.Javajaotan.Lib;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Javajaotan2Commands {
    static List<String> commands = new ArrayList<>();
    static long fetchTime = -1L;

    public static void fetch() {
        if (fetchTime >= System.currentTimeMillis() - 3600000L) {
            return;
        }
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
            Request request = new Request.Builder().url("http://localhost:31002/docs").build();

            try (Response response = client.newCall(request).execute()) {
                if (response.code() != 200 && response.code() != 302) {
                    return;
                }
                JSONArray array = new JSONArray(Objects.requireNonNull(response.body()).string());
                for (int i = 0; i < array.length(); i++) {
                    commands.add(array.getJSONObject(i).getString("name").toLowerCase());
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static boolean isImplemented(String command) {
        fetch();
        return commands.contains(command);
    }
}
