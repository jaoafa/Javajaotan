package com.jaoafa.Javajaotan.Task;

import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.*;

public class Task_3MonthCheck extends TimerTask {
    @Override
    public void run() {
        System.out.println("Task_3MonthCheck().run()");
        // MinecraftConnected
        JDA jda = Main.getJDA();
        Guild guild = jda.getGuildById(597378876556967936L); // new jMS Gamers Club
        if (guild == null) {
            System.out.println("guild not found.");
            return;
        }
        Role MinecraftConnectedRole = guild.getRoleById(604011598952136853L);
        Role SubAccountRole = guild.getRoleById(753047225751568474L);
        TextChannel channel = guild.getTextChannelById(597419057251090443L); // new general
        if (channel == null) {
            System.out.println("[3MonthCheck] general(597419057251090443) channel is not found.");
            return;
        }
        List<MinecraftDiscordConnection> connections = new ArrayList<>();
        try {
            MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM discordlink WHERE disabled = ?");
            statement.setBoolean(1, false);
            ResultSet res = statement.executeQuery();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM login WHERE uuid = ? AND login_success = ? ORDER BY id DESC LIMIT 1");
            while (res.next()) {
                stmt.setString(1, res.getString("uuid"));
                stmt.setBoolean(2, true);
                ResultSet result = stmt.executeQuery();
                Date loginDate = null;
                if (result.next()) {
                    loginDate = result.getDate("date");
                }
                connections.add(new MinecraftDiscordConnection(
                        res.getString("player"),
                        UUID.fromString(res.getString("uuid")),
                        res.getString("disid"),
                        res.getString("discriminator"),
                        loginDate,
                        res.getTimestamp("expired_date")
                ));
                result.close();
            }
            stmt.close();
            res.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        guild.loadMembers().onSuccess(members -> members.forEach(member -> {
            Optional<MinecraftDiscordConnection> connection = connections.stream().filter(conn -> conn.getDiscordUserID().equals(member.getId())).findFirst();
            if (!connection.isPresent()) {
                return;
            }
            check(guild, MinecraftConnectedRole, SubAccountRole, connection.get(), channel, member);
        }))
                .onError(err -> {
                    System.out.println("[Task_3MonthCheck] Error: " + err.getClass().getName());
                    err.printStackTrace();
                });
    }

    private void check(Guild guild, Role MinecraftConnectedRole, Role SubAccountRole, MinecraftDiscordConnection connection, TextChannel channel, Member member) {
        boolean isSubAccount = member.getRoles().stream().anyMatch(_role -> _role.getIdLong() == SubAccountRole.getIdLong());
        if (member.getUser().isBot()) {
            // bot
            return;
        }
        if (isSubAccount) {
            // Sub Account : ok
            return;
        }
        Instant instant = Instant.ofEpochMilli(connection.getLoginDate().getTime());
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime joinTime = LocalDateTime.ofInstant(instant, zone);
        LocalDateTime now = LocalDateTime.now();
        long diffDays = ChronoUnit.DAYS.between(joinTime, now);
        if (diffDays <= 90) {
            // 90日(3か月)以内
            return;
        }
        if (!connection.isLinkExpired()) {
            return; // 期限前の場合除外。期限が設けられていない(通常)の場合必ずtrueになるので、ここは通らない。
        }

        System.out.println("[3MonthCheck] Remove Link: " + member.getUser().getName() + "#" + member.getUser().getDiscriminator()
                + " | between: " + diffDays + "days. | expired_date: " + connection.getLinkExpiredDate());

        if (new Date().before(new Date(1612364400000L))) { // 2021-02-04 00:00:00
            return;
        }
        try {
            MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn.prepareStatement("UPDATE discordlink SET disabled = ? WHERE disid = ?");
            statement.setBoolean(1, true);
            statement.setString(2, member.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        guild.removeRoleFromMember(member, MinecraftConnectedRole).queue(
                success -> channel.sendMessage(member.getAsMention() + ", :bangbang:あなたのDiscordアカウントに接続されていたMinecraftアカウント「`" + connection.getPlayerName() + "`」が最終ログインから3ヶ月経過致しました。\nサーバルール及び個別規約により、建築物や自治体の所有権がなくなり、Minecraftアカウントとの接続が自動的に切断されました。").queue(),
                failure -> Main.ReportChannel
                        .sendMessage(String.format("3MonthCheckにて最終ログインから3か月を経過したためユーザー「%s」のロールを解除しようとしましたが正常に実行できませんでした！\n**Message**: `%s | %s`", member.getUser().getAsTag(), failure.getClass().getName(), failure.getMessage()))
                        .queue()
        );
    }

    static class MinecraftDiscordConnection {
        private String player;
        private UUID uuid;
        private String disid;
        private String discriminator;
        private Date loginDate;
        private Timestamp link_expired_date;

        public MinecraftDiscordConnection(String player, UUID uuid, String disid, String discriminator, Date loginDate, Timestamp link_expired_date) {
            this.player = player;
            this.uuid = uuid;
            this.disid = disid;
            this.discriminator = discriminator;
            this.loginDate = loginDate;
            this.link_expired_date = link_expired_date;
        }

        public String getPlayerName() {
            return player;
        }

        public void setPlayerName(String player) {
            this.player = player;
        }

        public UUID getUUID() {
            return uuid;
        }

        public void setUUID(UUID uuid) {
            this.uuid = uuid;
        }

        public String getDiscordUserID() {
            return disid;
        }

        public void setDiscordUserID(String disid) {
            this.disid = disid;
        }

        public String getDiscriminator() {
            return discriminator;
        }

        public void setDiscriminator(String discriminator) {
            this.discriminator = discriminator;
        }

        public Date getLoginDate() {
            return loginDate;
        }

        public void setLoginDate(Date loginDate) {
            this.loginDate = loginDate;
        }

        public Timestamp getLinkExpiredDate() {
            return link_expired_date;
        }

        public boolean isLinkExpired() {
            if (link_expired_date == null) return true;
            return link_expired_date.after(Timestamp.from(Instant.now()));
        }

        public void setLink_expired_date(Timestamp link_expired_date) {
            this.link_expired_date = link_expired_date;
        }
    }
}
