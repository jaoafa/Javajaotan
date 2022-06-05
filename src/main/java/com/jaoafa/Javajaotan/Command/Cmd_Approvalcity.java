package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.sql.*;

public class Cmd_Approvalcity implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (channel.getIdLong() != 597423467796758529L) {
            message.reply("このチャンネルではこのコマンドを使用することはできません。").queue();
            return; // #meeting以外
        }
        if (args.length != 2) {
            message.reply("引数が不正です。").queue();
            return;
        }
        String type = args[0];
        if (!Library.isInt(args[1])) {
            message.reply("RequestIDが不正です。int型で指定してください。").queue();
            return;
        }
        int reqID = Integer.parseInt(args[1]);

        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            message.reply("MySQLDBManagerがロードされていません。").queue();
            return;
        }

        if (type.equalsIgnoreCase("create")) {
            // 自治体新規登録申請
            ApprovalCreate(MySQLDBManager, message, reqID);
            return;
        } else if (type.equalsIgnoreCase("corners")) {
            // 自治体範囲変更申請
            ApprovalCorners(MySQLDBManager, message, reqID);
            return;
        }
        message.reply("指定されたTypeは未実装です。").queue();
    }

    void ApprovalCreate(MySQLDBManager MySQLDBManager, Message message, int reqID) {
        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities_new_waiting WHERE id = ? AND status = 0");
            statement.setInt(1, reqID);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                message.reply("指定されたRequestIDのリクエストが見つかりません。").queue();
                return;
            }

            String name = res.getString("name");
            String namekana = res.getString("namekana");
            String regionname = res.getString("regionname");
            String player = res.getString("player");
            String uuid = res.getString("uuid");
            String discord_userid = res.getString("discord_userid");
            String summary = res.getString("summary");
            String name_origin = res.getString("name_origin");
            String corners = res.getString("corners");
            String blocknum = res.getString("blocknum");
            String reason = res.getString("reason");

            PreparedStatement stmt_insert = conn
                    .prepareStatement(
                            "INSERT INTO cities (name, namekana, regionname, player, uuid, discord_userid, summary, name_origin, blocknum, corners, reason, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                            Statement.RETURN_GENERATED_KEYS);
            stmt_insert.setString(1, name);
            stmt_insert.setString(2, namekana);
            stmt_insert.setString(3, regionname);
            stmt_insert.setString(4, player);
            stmt_insert.setString(5, uuid);
            stmt_insert.setString(6, discord_userid);
            stmt_insert.setString(7, summary);
            stmt_insert.setString(8, name_origin);
            stmt_insert.setString(9, blocknum);
            stmt_insert.setString(10, corners);
            stmt_insert.setString(11, reason);
            stmt_insert.executeUpdate();

            ResultSet r = stmt_insert.getGeneratedKeys();

            int cities_id = -1;
            if (r.next()) {
                cities_id = r.getInt(1);
            }
            stmt_insert.close();

            res.close();
            statement.close();

            PreparedStatement statement_update = conn
                .prepareStatement("UPDATE cities_new_waiting SET status = ?, city_id = ? WHERE id = ?");
            statement_update.setInt(1, 1);
            statement_update.setInt(2, cities_id);
            statement_update.setInt(3, reqID);
            statement_update.executeUpdate();
            statement_update.close();

            String warnMsg = "・自治体を紹介するまたは自治体ルールなどを記載する「自治体ページ」の制作はjaopediaでお願いします。 https://wiki.jaoafa.com/\n"
                + "・自治体は最終ログインから3か月が経過した場合、自治体の所有権がなくなり運営管轄となります。最低限、ログインをある程度継続していたただきますようお願い申し上げます。(基本的に、2か月経過後運営からDiscordにて連絡いたします)\n"
                + "・その他のルールについては自治体関連方針にてご確認ください。 https://jaoafa.com/rule/management/cities";

            TextChannel city_request = Main.getJDA().getTextChannelById(709008822043148340L);
            if (city_request == null) {
                System.out.println("[Cmd_Approvalcity|ApprovalCreate] getTextChannelById(#city_request) == null");
                return;
            }
            city_request.sendMessage(String.format("<@%s> 自治体「`%s`」の自治体新規登録申請を**承認**しました(リクエストID: %d)。保護名は`%s`です。\n(%sブロック / 自治体内部管理ID: %d)\n```%s```", discord_userid, name, reqID, regionname, blocknum, cities_id, warnMsg)).queue();

            message.reply("自治体新規登録申請の承認処理を完了しました。").queue();
        } catch (SQLException e) {
            e.printStackTrace();
            message.reply("自治体新規登録申請の承認処理に失敗しました。").queue();
        }
    }

    void ApprovalCorners(MySQLDBManager MySQLDBManager, Message message, int reqID) {
        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities_corners_waiting WHERE id = ? AND status = 0");
            statement.setInt(1, reqID);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                message.reply("指定されたRequestIDのリクエストが見つかりません。").queue();
                res.close();
                statement.close();
                return;
            }

            int cities_id = res.getInt("cities_id");
            String corners_new = res.getString("corners_new");

            PreparedStatement stmt_cities = conn
                    .prepareStatement("SELECT * FROM cities WHERE id = ?");
            stmt_cities.setInt(1, cities_id);
            ResultSet res_cities = stmt_cities.executeQuery();

            if (!res_cities.next()) {
                message.reply("自治体IDに合致する自治体情報が見つかりません。").queue();
                res.close();
                statement.close();
                res_cities.close();
                stmt_cities.close();
                return;
            }

            String name = res_cities.getString("name");
            String discord_userid = res_cities.getString("discord_userid");

            PreparedStatement stmt_insert = conn
                    .prepareStatement("UPDATE cities SET corners = ? WHERE id = ?");
            stmt_insert.setString(1, corners_new);
            stmt_insert.setInt(2, cities_id);
            stmt_insert.executeUpdate();
            stmt_insert.close();

            res.close();
            statement.close();
            res_cities.close();
            stmt_cities.close();

            PreparedStatement statement_update = conn
                    .prepareStatement("UPDATE cities_corners_waiting SET status = ? WHERE id = ?");
            statement_update.setInt(1, 1);
            statement_update.setInt(2, reqID);
            statement_update.executeUpdate();
            statement_update.close();

            TextChannel city_request = Main.getJDA().getTextChannelById(709008822043148340L);
            if (city_request == null) {
                System.out.println("[Cmd_Approvalcity|ApprovalCorners] getTextChannelById(#city_request) == null");
                return;
            }
            city_request.sendMessage(String.format("<@%s> 自治体「`%s` (%d)」の自治体範囲変更申請を**承認**しました。(リクエストID: %d)", discord_userid, name, cities_id, reqID)).queue();

            message.reply("自治体範囲変更申請の承認処理を完了しました。").queue();
        } catch (SQLException e) {
            e.printStackTrace();
            message.reply("自治体範囲変更申請の承認処理に失敗しました。").queue();
        }
    }

    @Override
    public String getDescription() {
        return "自治体関連の承認処理を行います。特定チャンネルでのみ使用できます。";
    }

    @Override
    public String getUsage() {
        return "/approvalcity <create|corners> <RequestID>";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}