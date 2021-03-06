package com.jaoafa.Javajaotan.Task;

import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class Task_VerifiedCheck extends TimerTask {
    @Override
    public void run() {
        System.out.println("Task_VerifiedCheck().run()");
        // 参加から10分以内に発言がなかったら蹴る
        JDA jda = Main.getJDA();
        Guild guild = jda.getGuildById(597378876556967936L); // new jMS Gamers Club
        if (guild == null) {
            System.out.println("guild not found.");
            return;
        }
        Role role = guild.getRoleById(597421078817669121L);
        TextChannel channel = guild.getTextChannelById(597419057251090443L); // new general
        if (channel == null) {
            System.out.println("[VerifiedError] general(597419057251090443) channel is not found.");
            return;
        }
        guild.loadMembers()
            .onSuccess(members -> members.forEach(member -> check(guild, role, channel, member)))
            .onError(err -> {
                System.out.println("[Task_VerifiedCheck] Error: " + err.getClass().getName());
                err.printStackTrace();
            });
    }

    private void check(Guild guild, Role role, TextChannel channel, Member member) {
        List<Role> roles = member.getRoles().stream().filter(_role -> _role.getIdLong() == role.getIdLong())
                .collect(Collectors.toList());
        if (roles.size() != 0) {
            // role exists : ok
            return;
        }
        if (member.getUser().isBot()) {
            // bot
            return;
        }
        LocalDateTime joinTime = member.getTimeJoined().atZoneSameInstant(ZoneId.of("Asia/Tokyo")).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long diffmin = ChronoUnit.MINUTES.between(joinTime, now);
        if (diffmin <= 10) {
            // 10分以内
            return;
        }

        System.out.println("[Task_VerifiedCheck] kick: " + member.getUser().getName() + "#" + member.getUser().getDiscriminator()
                + " | between: " + diffmin + "min.");
        guild.kick(member).queue(
                success -> channel.sendMessage(String.format(":wave:チャットがないまま10分を経過したため、ユーザー「`%s`」をキックしました。", member.getUser().getAsTag())).queue(),
                failure -> Main.ReportChannel
                        .sendMessage(String.format("Task_VerifiedCheckにてチャットがないまま10分を経過したためユーザー「`%s`」をキックしようとしましたが正常に実行できませんでした！\n**Message**: `%s | %s`", member.getUser().getAsTag(), failure.getClass().getName(), failure.getMessage()))
                        .queue()
        );
    }
}
