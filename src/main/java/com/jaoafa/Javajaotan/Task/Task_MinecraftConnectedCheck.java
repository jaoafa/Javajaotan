package com.jaoafa.Javajaotan.Task;

import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimerTask;

public class Task_MinecraftConnectedCheck extends TimerTask {
    @Override
    public void run() {
        System.out.println("Task_MinecraftConnectedCheck().run()");
        // MinecraftConnected
        JDA jda = Main.getJDA();
        Guild guild = jda.getGuildById(597378876556967936L); // new jMS Gamers Club
        if (guild == null) {
            System.out.println("guild not found.");
            return;
        }
        Role MinecraftConnectedRole = guild.getRoleById(604011598952136853L);
        Role SubAccountRole = guild.getRoleById(753047225751568474L);
        Role NeedSupportRole = jda.getRoleById(786110419470254102L);
        TextChannel channel = guild.getTextChannelById(597419057251090443L); // new general
        if (channel == null) {
            System.out.println("[MinecraftConnectedCheck] general(597419057251090443) channel is not found.");
            return;
        }
        guild.loadMembers()
            .onSuccess(members -> members.forEach(member -> check(guild, MinecraftConnectedRole, SubAccountRole, NeedSupportRole, channel, member)))
            .onError(err -> {
                System.out.println("[Task_MinecraftConnectedCheck] Error: " + err.getClass().getName());
                err.printStackTrace();
            });
    }

    private void check(Guild guild, Role MinecraftConnectedRole, Role SubAccountRole, Role NeedSupportRole, TextChannel channel, Member member) {
        boolean isMinecraftConnected = member.getRoles().stream().anyMatch(_role -> _role.getIdLong() == MinecraftConnectedRole.getIdLong());
        boolean isSubAccount = member.getRoles().stream().anyMatch(_role -> _role.getIdLong() == SubAccountRole.getIdLong());
        boolean isNeedSupport = member.getRoles().stream().anyMatch(_role -> _role.getIdLong() == NeedSupportRole.getIdLong());
        if (member.getUser().isBot()) {
            // bot
            return;
        }
        if (isMinecraftConnected) {
            // Minecraft Connected : ok
            return;
        }
        if (isSubAccount) {
            // Sub Account : ok
            return;
        }
        if (new Date().before(new Date(1599663600000L))) {
            return;
        }
        LocalDateTime joinTime = member.getTimeJoined().atZoneSameInstant(ZoneId.of("Asia/Tokyo")).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long diffweeks = ChronoUnit.WEEKS.between(joinTime, now);
        int needWeeks = isNeedSupport ? 3 : 1;
        if (diffweeks <= 1) {
            // 1週間以内
            return;
        }
        if (isNeedSupport && diffweeks <= 3) {
            // NeedSupport権限がついていてかつ3週間以内
            return;
        }

        try {
            MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM discordlink WHERE disid = ?");
            statement.setLong(1, member.getIdLong());
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                res.close();
                statement.close();
                return;
            }
            res.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        System.out.println("[Task_MinecraftConnectedCheck] kick: " + member.getUser().getName() + "#" + member.getUser().getDiscriminator()
                + " | between: " + diffweeks + "week.");
        guild.kick(member).queue(
                success -> channel.sendMessage(String.format(":wave:Minecraftアカウントとの連携が%d週間以上行われなかったため、ユーザー「`%s`」をキックしました。", needWeeks, member.getUser().getAsTag())).queue(),
                failure -> Main.ReportChannel
                        .sendMessage(String.format("Task_MinecraftConnectedCheckにてMinecraftアカウントとの連携が%d週間を経過したためユーザー「`%s`」をキックしようとしましたが正常に実行できませんでした！\n**Message**: `%s | %s`", needWeeks, member.getUser().getAsTag(), failure.getClass().getName(), failure.getMessage()))
                        .queue()
        );
    }
}
