package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.SQLiteDBManager;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cmd_Getatama implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (Library.isDenyToyCmd(channel)) {
            message.reply("このチャンネルではこのコマンドを利用できません。<#616995424154157080>などで実行してください。").queue();
            return;
        }
        File sqliteFile = new File("pp_dic_preset.sqlite");
        if (!sqliteFile.exists()) {
            // nasa
            message.reply("動作に必要なファイルが見つかりません。開発部にお問い合わせください。\n"
                    + "Reason: " + sqliteFile.getAbsolutePath() + " not found").queue();
            return;
        }
        Connection conn;
        try {
            SQLiteDBManager sqlite = new SQLiteDBManager(sqliteFile);
            conn = sqlite.getConnection();
        } catch (ClassNotFoundException | IOException | SQLException e) {
            message.reply("処理に失敗しました。時間を置いてもう一度お試しください。\nReason: " + e.getMessage())
                    .queue();
            Main.ExceptionReporter(message, e);
            return;
        }

        int count = 1;
        if (args.length == 1) {
            if (!Library.isInt(args[0])) {
                // not is int
                message.reply("数値を指定してください。").queue();
                return;
            }
            count = Integer.parseInt(args[0]);
            if (count > 100) {
                // count > 100 | 101↑ x
                message.reply("100以下で指定してください。").queue();
                return;
            }
        }
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement statement_prefix = conn
                    .prepareStatement("SELECT word FROM word_prefix ORDER BY RANDOM() LIMIT ?;");
            statement_prefix.setInt(1, count);
            ResultSet res_prefix = statement_prefix.executeQuery();
            while (res_prefix.next()) {
                list.add(res_prefix.getString("word"));
            }

            PreparedStatement statement_middle = conn
                    .prepareStatement("SELECT word FROM word_middle ORDER BY RANDOM() LIMIT ?;");
            statement_middle.setInt(1, count);
            ResultSet res_middle = statement_middle.executeQuery();
            while (res_middle.next()) {
                int row = res_middle.getRow() - 1;
                list.set(row, list.get(row) + res_middle.getString("word"));
            }

            PreparedStatement statement_suffix = conn
                    .prepareStatement("SELECT word FROM word_suffix ORDER BY RANDOM() LIMIT ?;");
            statement_suffix.setInt(1, count);
            ResultSet res_suffix = statement_suffix.executeQuery();
            while (res_suffix.next()) {
                int row = res_suffix.getRow() - 1;
                list.set(row, list.get(row) + res_suffix.getString("word"));
            }
        } catch (SQLException e) {
            message.reply("処理に失敗しました。時間を置いてもう一度お試しください。\n**Reason**: " + e.getMessage())
                    .queue();
            Main.ExceptionReporter(message, e);
            return;
        }
        message.reply("```" + String.join("\n", list) + "```").queue();
    }

    @Override
    public String getDescription() {
        return "アタマな言葉を生成します。";
    }

    @Override
    public String getUsage() {
        return "/getatama [Count]";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}