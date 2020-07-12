package com.jaoafa.Javajaotan.Command;

import java.awt.Polygon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Checkintersects implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。(`MySQLDBManager null`)")
					.queue();
			return;
		}
		Pattern p = Pattern.compile("#([0-9]+) \\D*?(\\-?[0-9]+) \\D*?(\\-?[0-9]+)");
		Matcher m = p.matcher(message.getContentRaw());

		if (m.groupCount() == 0) {
			channel.sendMessage(member.getAsMention() + ", 範囲情報が見つかりませんでした。")
					.queue();
			return;
		}

		Polygon polygon = new Polygon();
		while (m.find()) {
			String X_str = m.group(2);
			String Z_str = m.group(3);

			polygon.addPoint(Integer.valueOf(X_str), Integer.valueOf(Z_str));
			System.out.println("polygon.addPoint " + X_str + " " + Z_str);
		}

		String ret_message = "";
		boolean bool = true;

		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM cities");
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				int regID = res.getInt("id");
				String regName = res.getString("name");
				Polygon other_polygon = new Polygon();
				JSONArray corners = new JSONArray(res.getString("corners"));
				for (int i = 0; i < corners.length(); i++) {
					JSONObject obj = corners.getJSONObject(i);
					int x = obj.optInt("x", Integer.MIN_VALUE);
					int z = obj.optInt("z", Integer.MIN_VALUE);

					other_polygon.addPoint(x, z);
					System.out.println("other_polygon.addPoint " + x + " " + z);
				}

				if (polygon.intersects(other_polygon.getBounds2D())) {
					ret_message += "[" + regID + "] request region intersects " + regName + "\n";
					bool = false;
					continue;
				}

				if (polygon.contains(other_polygon.getBounds2D())) {
					ret_message += "[" + regID + "] request region contains " + regName + ".";
					bool = false;
					continue;
				}

				if (other_polygon.intersects(polygon.getBounds2D())) {
					ret_message += "[" + regID + "] " + regName + " intersects request region.";
					bool = false;
					continue;
				}

				if (other_polygon.contains(polygon.getBounds())) {
					ret_message += "[" + regID + "] " + regName + " contains request region.";
					bool = false;
					continue;
				}
			}
			if (bool) {
				channel.sendMessage(member.getAsMention() + ", OK.").queue();
			} else {
				channel.sendMessage(member.getAsMention() + ", NG.\nResponse: ```" + ret_message + "```").queue();
			}
			return;
		} catch (SQLException e) {
			channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。\n"
					+ "**Message**: `" + e.getMessage() + "`").queue();
			return;
		}
	}

	@Override
	public String getDescription() {
		return "IDをメモするメッセージを発言されたチャンネルに投稿します。";
	}

	@Override
	public String getUsage() {
		return "/recid";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}
