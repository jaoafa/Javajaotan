package com.jaoafa.Javajaotan.ALLChat;

import com.jaoafa.Javajaotan.ALLChatPremise;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ALL_AntiGai implements ALLChatPremise {
    static List<String> antis = new ArrayList<>();

    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        String text = message.getContentRaw();
        if (user.isBot()) {
            return;
        }
        if (message.getEmotes().isEmpty()) {
            return;
        }
        message.getEmotes().forEach(emote -> {
            String url = emote.getImageUrl();
            if (antis.contains(url)) {
                message.delete().queue();
                return;
            }
            try {
                OkHttpClient okclient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = okclient.newCall(request).execute();
                if (response.body() == null) {
                    return;
                }
                InputStream stream = response.body().byteStream();
                String hex = DigestUtils.sha1Hex(stream);
                stream.close();
                response.close();
                System.out.println(emote.getName() + "(" + emote.getId() + "): " + hex);

                boolean matched = false;
                if (hex.equals("f7899dcfe26a5c846bc35ff27d1c7e326ea6f11a")) {
                    // 691965036406636615.png
                    matched = true;
                    antis.add(url);
                }
                if (hex.equals("2782ca50872a90d35aba815e90847a03f26f63d3")) {
                    // 716544687858647060.png / 742042270412439732.png
                    matched = true;
                    antis.add(url);
                }
                if (hex.equals("ee93539cbbba6f253f8657964a2bff133e33a628")) {
                    // 716544687909109780.png / 742042270466834472.png
                    matched = true;
                    antis.add(url);
                }
                if (hex.equals("7ef41ad8b1098df7372fbcd5cea9f44783026490")) {
                    // 742042270097866794.png
                    matched = true;
                    antis.add(url);
                }

                if (matched) {
                    System.out.println("-> matched.");
                    message.delete().queue();
                }
            } catch (IOException e) {
                Main.ExceptionReporter(channel, e);
            }
        });
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return true;
    }
}