package com.jaoafa.Javajaotan.Task;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

import com.jaoafa.Javajaotan.Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class Task_VerifiedCheck extends TimerTask {
	@Override
	public void run() {
		// 参加から10分以内に発言がなかったら蹴る
		JDA jda = Main.getJDA();
		Guild guild = jda.getGuildById(597378876556967936L); // new jMS Gamers Club
		Role role = guild.getRoleById(597421078817669121L);
		TextChannel channel = guild.getTextChannelById(597419057251090443L); // new general
		if (channel == null) {
			System.out.println("[VerifiedError] general(597419057251090443) channel is not found.");
			return;
		}
		for (Member user : guild.getMembers()) {
			List<Role> roles = user.getRoles().stream().filter(_role -> _role.getIdLong() == role.getIdLong())
					.collect(Collectors.toList());
			if (roles.size() != 0) {
				// role exists : ok
				continue;
			}
			if (user.getUser().isBot()) {
				// bot
				continue;
			}
			LocalDateTime joinTime = user.getTimeJoined().atZoneSameInstant(ZoneId.of("Asia/Tokyo")).toLocalDateTime();
			LocalDateTime now = LocalDateTime.now();
			long diffmin = ChronoUnit.MINUTES.between(joinTime, now);
			if (diffmin <= 10) {
				// 10分以内
				continue;
			}

			System.out.println("kick: " + user.getUser().getName() + "#" + user.getUser().getDiscriminator()
					+ " | between: " + diffmin + "min.");
			guild.kick(user).queue(success -> {
				channel.sendMessage(":wave:チャットがないまま10分を経過したため、ユーザー「" + user.getUser().getName() + "#"
						+ user.getUser().getDiscriminator() + "」をキックしました。").queue();
			}, failure -> {
				Main.ReportChannel
						.sendMessage("Task_VerifiedCheckにてチャットがないまま10分を経過したためユーザー「" + user.getUser().getName() + "#"
								+ user.getUser().getDiscriminator() + "」をキックしようとしましたが正常に実行できませんでした！\n**Message**: `"
								+ failure.getClass().getName() + " | " + failure.getMessage() + "`")
						.queue();
			});

		}
	}
}
