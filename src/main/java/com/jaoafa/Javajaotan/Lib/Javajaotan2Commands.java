package com.jaoafa.Javajaotan.Lib;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Javajaotan2Commands {
    static List<String> commands = new ArrayList<>();
    static long fetchTime = -1L;

    public static void fetch() {
        if (fetchTime >= System.currentTimeMillis() - 3600000L) {
            return;
        }
        /*
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
         */

        // implement-commands.json を読み込む
        try {
            JSONArray array = new JSONArray(Files.readString(Paths.get("implement-commands.json")));
            for (int i = 0; i < array.length(); i++) {
                commands.add(array.getJSONObject(i).getString("name").toLowerCase());
            }
        } catch (IOException ignored) {
        }
    }

    public static boolean isImplemented(String command) {
        fetch();
        return commands.contains(command);
    }
}
