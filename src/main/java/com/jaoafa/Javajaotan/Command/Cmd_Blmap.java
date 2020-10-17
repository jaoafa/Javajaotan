package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Cmd_Blmap implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。(`MySQLDBManager null`)")
                    .queue();
            return;
        }
        if (channel.getIdLong() != 597423444501463040L) {
            channel.sendMessage(member.getAsMention() + ", このチャンネルでは使用できません。").queue();
            return;
        }
        if (args.length == 0) {
            channel.sendMessage(member.getAsMention() + ", 引数が足りません。\n" + getUsage()).queue();
            return;
        }
        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM login WHERE player = ?");
            statement.setString(1, args[0]);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                channel.sendMessage(member.getAsMention() + ", 指定されたユーザーは見つかりません。").queue();
                res.close();
                statement.close();
                return;
            }

            String uuid = res.getString("uuid");
            res.close();
            statement.close();

            String url = "https://api.jaoafa.com/cities/getblockimg?uuid=" + uuid;

            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder().url(url).build();

                Response response = client.newCall(request).execute();
                if (response.code() != 200 && response.code() != 302) {
                    channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: " + response.code() + " "
                            + response.body().string()).queue();
                    response.close();
                    return;
                }

                channel.sendFile(response.body().byteStream(), uuid + ".png").append(member.getAsMention()).complete();
                response.close();
            } catch (IOException ex) {
                channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: " + ex.getMessage()).queue();
            }
        } catch (SQLException e) {
            channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。\n"
                    + "**Message**: `" + e.getMessage() + "`").queue();
        }
    }

    @Override
    public String getDescription() {
        return "ブロックの編集情報を示した画像を投稿します。特定のチャンネルで使用できます。";
    }

    @Override
    public String getUsage() {
        return "/blmap <PlayerName>";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}
