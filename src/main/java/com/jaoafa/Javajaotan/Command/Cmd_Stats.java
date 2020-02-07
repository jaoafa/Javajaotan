package com.jaoafa.Javajaotan.Command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Stats implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length == 1) {
			channel.sendMessage(member.getAsMention() + ", ```" + getStats(message, args[0]) + "```").queue();
			return;
		}
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。(`MySQLDBManager null`)")
					.queue();
			return;
		}
		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM discordlink WHERE disid = ? AND disabled = 0");
			statement.setString(1, member.getId());
			ResultSet res = statement.executeQuery();
			if (res.next()) {
				channel.sendMessage(member.getAsMention() + ", ```" + getStats(message, res.getString("uuid")) + "```")
						.queue();
			} else {
				channel.sendMessage(member.getAsMention() + ", please `/link`").queue();
			}
			return;
		} catch (SQLException e) {
			channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。\n"
					+ "**Message**: `" + e.getMessage() + "`").queue();
			return;
		}
	}

	private String getStats(Message message, String uuid) {
		return getRunCommand(message, "php", "/var/jaoafa/Javajaotan/stats/main.php", uuid);
	}

	private String getRunCommand(Message message, String... command) {
		MessageChannel channel = message.getChannel();
		Process p;
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(command);
			builder.directory(new File("/var/jaoafa/Javajaotan/stats/"));
			builder.redirectErrorStream(true);
			p = builder.start();
			p.waitFor(10, TimeUnit.MINUTES);
		} catch (IOException e) {
			Main.ExceptionReporter(channel, e);
			return "null IOException";
		} catch (InterruptedException e) {
			Main.ExceptionReporter(channel, e);
			return "null InterruptedException";
		}
		InputStream is = p.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String text = "";
		try {
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				text += line + "\n";
			}
			br.close();
			is.close();
		} catch (IOException e) {
			Main.ExceptionReporter(channel, e);
			return "null IOException";
		}
		return text;
	}

	@Override
	public String getDescription() {
		return "今までのMinecraft鯖内オンライン時間情報を表示します。";
	}

	@Override
	public String getUsage() {
		return "/stats [UUID]";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}

}
