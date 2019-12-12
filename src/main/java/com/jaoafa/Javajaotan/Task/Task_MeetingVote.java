package com.jaoafa.Javajaotan.Task;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.Library;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Task_MeetingVote extends TimerTask {
	int TeamJaoCount = 9; // Admin + Moderator
	Pattern p = Pattern.compile("\\[Border:([0-9]+)\\]");
	boolean debugMode = false;

	public Task_MeetingVote() {

	}

	public Task_MeetingVote(boolean debugMode) {
		this.debugMode = debugMode;
	}

	@Override
	public void run() {
		JDA jda = Main.getJDA();

		double divided = TeamJaoCount / 2;
		int VoteBorder = (int) Math.ceil(divided);
		if (TeamJaoCount % 2 == 0) {
			VoteBorder++;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		TextChannel channel = jda.getTextChannelById(597423974816808970L);
		List<Message> messages = channel.retrievePinnedMessages().complete();
		for (Message message : messages) {
			String content = message.getContentDisplay();
			LocalDateTime timestamp = message.getTimeCreated().atZoneSameInstant(ZoneId.of("Asia/Tokyo"))
					.toLocalDateTime();

			List<User> good = message.retrieveReactionUsers("\uD83D\uDC4D").complete();
			int good_count = good.size();
			List<User> bad = message.retrieveReactionUsers("\uD83D\uDC4E").complete();
			int bad_count = bad.size();
			List<User> white = message.retrieveReactionUsers("🏳️").complete();
			int white_count = white.size();

			int _VoteBorder = VoteBorder;
			if (content.contains("\n")) {
				String[] contents = content.split("\n");
				Matcher m = p.matcher(contents[0]);
				if (m.find()) {
					if (!Library.isInt(m.group(1))) {
						continue;
					}
					_VoteBorder = Integer.valueOf(m.group(1));
				}
			}
			int _white = white_count;
			if (_white != 0) {
				if (_VoteBorder % 2 == 0) {
					_VoteBorder--;
					_white--;
				}
				_VoteBorder -= _white / 2;
			}
			if (debugMode) {
				System.out.println("MeetingVote[debugMode] " + message.getId() + " by "
						+ message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator());
				System.out.println("MeetingVote[debugMode] " + "VoteBorder: " + VoteBorder + " / Good: " + good_count
						+ " / Bad: " + bad_count + " / White: " + white_count + " / _VoteBorder: " + _VoteBorder);
			}

			if (good_count >= _VoteBorder) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("VOTE RESULT");
				builder.setDescription("@here :thumbsup:投票が承認されたことをお知らせします。");
				builder.addField("賛成 / 反対 / 白票", good_count + " / " + bad_count + " / " + white_count, false);
				builder.addField("決議ボーダー", String.valueOf(_VoteBorder), false);
				builder.addField("内容", content, false);
				builder.addField("対象メッセージURL", "https://discordapp.com/channels/" + message.getGuild().getId()
						+ "/" + message.getChannel().getId() + "/" + message.getId(), false);
				builder.addField("投票開始日時",
						sdf.format(new Date(timestamp.toEpochSecond(ZoneOffset.ofHours(9)) * 1000)), false);
				builder.setColor(Color.GREEN);
				channel.sendMessage(builder.build()).queue();
				message.unpin().queue();
			} else if (bad_count >= _VoteBorder) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("VOTE RESULT");
				builder.setDescription("@here :thumbsup:投票が否認されたことをお知らせします。");
				builder.addField("賛成 / 反対 / 白票", good_count + " / " + bad_count + " / " + white_count, false);
				builder.addField("決議ボーダー", String.valueOf(_VoteBorder), false);
				builder.addField("内容", content, false);
				builder.addField("対象メッセージURL", "https://discordapp.com/channels/" + message.getGuild().getId()
						+ "/" + message.getChannel().getId() + "/" + message.getId(), false);
				builder.addField("投票開始日時",
						sdf.format(new Date(timestamp.toEpochSecond(ZoneOffset.ofHours(9)) * 1000)), false);
				builder.setColor(Color.RED);
				channel.sendMessage(builder.build()).queue();
				message.unpin().queue();
			}

			long start = timestamp.toEpochSecond(ZoneOffset.ofHours(9));
			long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(TimeUnit.SECONDS.toMillis(start));
			cal.add(Calendar.WEEK_OF_YEAR, 2);

			if ((start + 1209600) <= now) {
				// 2週間 1209600秒
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("VOTE RESULT");
				builder.setDescription("@here :wave:有効会議期限を過ぎたため、投票が否認されたことをお知らせします。");
				builder.addField("賛成 / 反対 / 白票", good_count + " / " + bad_count + " / " + white_count, false);
				builder.addField("決議ボーダー", String.valueOf(_VoteBorder), false);
				builder.addField("内容", content, false);
				builder.addField("対象メッセージURL", "https://discordapp.com/channels/" + message.getGuild().getId()
						+ "/" + message.getChannel().getId() + "/" + message.getId(), false);
				builder.addField("投票開始日時",
						sdf.format(timestamp.toEpochSecond(ZoneOffset.ofHours(9))) + " ("
								+ timestamp.toEpochSecond(ZoneOffset.ofHours(9)) + ")",
						false);
				builder.addField("有効会議期限",
						sdf.format(cal.getTime()) + " (" + TimeUnit.MILLISECONDS.toSeconds(cal.getTimeInMillis()) + ")",
						false);
				builder.addField("現在時刻",
						sdf.format(new Date(TimeUnit.SECONDS.toMillis(now))) + " (" + now + ")",
						false);
				builder.setColor(Color.ORANGE);
				channel.sendMessage(builder.build()).queue();
				message.unpin().queue();
			}
		}
	}
}
