package com.jaoafa.Javajaotan.Task;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;

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

				autoBadCitiesRequest(message);
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

				autoBadCitiesRequest(message);
			}
		}
	}

	private void autoBadCitiesRequest(Message message) {
		String contents = message.getContentRaw();
		if (!contents.startsWith("[API-CITIES-")) {
			return;
		}

		autoBad_CREATE_WAITING(contents);
		autoBad_CHANGE_CORNERS(contents);
		autoBad_CHANGE_OTHER(contents);
	}

	private void autoBad_CREATE_WAITING(String contents) {
		Pattern p = Pattern.compile("\\[API-CITIES-CREATE-WAITING:([0-9]+)\\]");
		Matcher m = p.matcher(contents);
		if (!m.find()) {
			return;
		}

		int id = Integer.parseInt(m.group(1));

		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return;
		}

		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM cities_new_waiting WHERE id = ?");
			statement.setInt(1, id);
			ResultSet res = statement.executeQuery();

			if (!res.next()) {
				return;
			}

			int reqid = res.getInt("id");
			String discord_userid = res.getString("discord_userid");
			String cities_name = res.getString("name");
			res.close();
			statement.close();

			Main.getJDA().getTextChannelById(597423370589700098L).sendMessage("<@" + discord_userid + "> 自治体「"
					+ cities_name + "」の自治体新規登録申請を**否認**しました。(リクエストID: " + reqid + ")");

			PreparedStatement statement_update = conn
					.prepareStatement("UPDATE cities_new_waiting SET status = ? WHERE id = ?");
			statement_update.setInt(1, -1);
			statement_update.setInt(2, id);
			statement_update.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void autoBad_CHANGE_CORNERS(String contents) {
		Pattern p = Pattern.compile("\\[API-CITIES-CHANGE-CORNERS-WAITING:([0-9]+)\\]");
		Matcher m = p.matcher(contents);
		if (!m.find()) {
			return;
		}

		int id = Integer.parseInt(m.group(1));

		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return;
		}

		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM cities_new_waiting WHERE id = ?");
			statement.setInt(1, id);
			ResultSet res = statement.executeQuery();

			if (!res.next()) {
				return;
			}

			int reqid = res.getInt("id");
			int cities_id = res.getInt("cities_id");
			res.close();
			statement.close();

			String discord_userid = getDiscordUserID(conn, cities_id);
			String cities_name = getCitiesName(conn, cities_id);

			Main.getJDA().getTextChannelById(597423370589700098L).sendMessage("<@" + discord_userid + "> 自治体「"
					+ cities_name + " (" + cities_id + ")」の自治体範囲変更申請を**否認**しました。(リクエストID: " + reqid + ")");

			PreparedStatement statement_update = conn
					.prepareStatement("UPDATE cities_corners_waiting SET status = ? WHERE id = ?");
			statement_update.setInt(1, -1);
			statement_update.setInt(2, id);
			statement_update.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void autoBad_CHANGE_OTHER(String contents) {
		Pattern p = Pattern.compile("\\[API-CITIES-CHANGE-OTHER-WAITING:([0-9]+)\\]");
		Matcher m = p.matcher(contents);
		if (!m.find()) {
			return;
		}

		int id = Integer.parseInt(m.group(1));

		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return;
		}

		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM cities_other_waiting WHERE id = ?");
			statement.setInt(1, id);
			ResultSet res = statement.executeQuery();

			if (!res.next()) {
				return;
			}

			int reqid = res.getInt("id");
			int cities_id = res.getInt("cities_id");
			res.close();
			statement.close();

			String discord_userid = getDiscordUserID(conn, cities_id);
			String cities_name = getCitiesName(conn, cities_id);

			Main.getJDA().getTextChannelById(597423370589700098L).sendMessage("<@" + discord_userid + "> 自治体「"
					+ cities_name + " (" + cities_id + ")」の自治体情報変更申請を**否認**しました。(リクエストID: " + reqid + ")");

			PreparedStatement statement_update = conn
					.prepareStatement("UPDATE cities_new_waiting SET status = ? WHERE id = ?");
			statement_update.setInt(1, -1);
			statement_update.setInt(2, id);
			statement_update.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String getDiscordUserID(Connection conn, int cities_id) {
		try {
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM cities WHERE id = ?");
			statement.setInt(1, cities_id);
			ResultSet res = statement.executeQuery();

			if (!res.next()) {
				return null;
			}

			String discorduserid = res.getString("discord_userid");
			res.close();
			statement.close();
			return discorduserid;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getCitiesName(Connection conn, int cities_id) {
		try {
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM cities WHERE id = ?");
			statement.setInt(1, cities_id);
			ResultSet res = statement.executeQuery();

			if (!res.next()) {
				return null;
			}

			String discorduserid = res.getString("name");
			res.close();
			statement.close();
			return discorduserid;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
