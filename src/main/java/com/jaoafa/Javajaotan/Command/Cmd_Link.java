package com.jaoafa.Javajaotan.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.RandomStringUtils;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Link implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。(`MySQLDBManager null`)")
					.queue();
			return;
		}
		try {
			Connection conn = MySQLDBManager.getConnection();
			String authkey = getAuthKey(conn);
			if (authkey == null) {
				channel.sendMessage(member.getAsMention() + ", AuthKeyを生成できませんでした。時間をおいて再度お試しください。")
						.queue();
				return;
			}
			String name = member.getUser().getName();
			String disid = member.getUser().getId();
			String discriminator = member.getUser().getDiscriminator();
			PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO discordlink_waiting (authkey, name, disid, discriminator) VALUES (?, ?, ?, ?);");
			statement.setString(1, authkey);
			statement.setString(2, name);
			statement.setString(3, disid);
			statement.setString(4, discriminator);
			statement.executeUpdate();

			member.getUser().openPrivateChannel().queue(dm -> {
				dm.sendMessage("このメッセージはjao Minecraft Server Discordのアカウント認証メッセージです。\n"
						+ "**jao Minecraft Serverに入り**、以下コマンドを実行してください。").queue();
				dm.sendMessage("```/discordlink " + authkey + "```").queue();
			}, failure -> {
				channel.sendMessage(member.getAsMention() + ", 個人メッセージへの送信に失敗しました: `" + failure.getMessage() + "`")
						.queue();
			});
			channel.sendMessage(member.getAsMention() + ", 個人メッセージに送信されたメッセージを確認し、指定された行動を行って下さい。\n"
					+ "メッセージが送信されてきませんか？何度か実行し直して正常動作しなければ開発部にお問い合わせをお願いします！").queue();
			return;
		} catch (SQLException e) {
			channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。\n"
					+ "**Message**: `" + e.getMessage() + "`").queue();
			return;
		}
	}

	@Override
	public String getDescription() {
		return "「Minecraft-Discord Connect」を行うためのAuthKeyを発行します。";
	}

	@Override
	public String getUsage() {
		return "/link";
	}

	String getAuthKey(Connection conn) throws SQLException {
		String authkey = null;
		while (true) {
			authkey = RandomStringUtils.randomAlphabetic(10);
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM discordlink_waiting WHERE authkey = ?");
			statement.setString(1, authkey);
			ResultSet res = statement.executeQuery();
			if (!res.next()) {
				return authkey;
			}
		}
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}

}
