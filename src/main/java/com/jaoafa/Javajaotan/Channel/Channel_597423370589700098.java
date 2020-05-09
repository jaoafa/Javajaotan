package com.jaoafa.Javajaotan.Channel;

import java.awt.Polygon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jaoafa.Javajaotan.ChannelPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;

public class Channel_597423370589700098 implements ChannelPremise {
	// #support
	@Override
	public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
			boolean edited) {
		if (message.getType() != MessageType.DEFAULT) {
			return;
		}
		String text = message.getContentRaw();
		Pattern p = Pattern.compile("#([0-9]+) \\D*?(\\-?[0-9]+) \\D*?(\\-?[0-9]+)");
		Matcher m = p.matcher(text);

		if (m.groupCount() == 0) {
			return;
		}

		String debug = "";
		LinkedList<Integer> X = new LinkedList<>();
		LinkedList<Integer> Z = new LinkedList<>();
		Polygon polygon = new Polygon();
		while (m.find()) {
			String keynum = m.group(1);
			String X_str = m.group(2);
			String Z_str = m.group(3);

			X.add(Integer.valueOf(X_str));
			Z.add(Integer.valueOf(Z_str));

			polygon.addPoint(Integer.valueOf(X_str), Integer.valueOf(Z_str));
			System.out.println("polygon.addPoint " + X_str + " " + Z_str);

			debug += "Added #" + keynum + " : " + X_str + " " + Z_str + "\n";
		}

		if (X.size() == 0 || Z.size() == 0) {
			return;
		}

		double blocks = Library.calcBlockNumber(X, Z);

		String system_msg = "特に問題はありません。";
		if (!Library.checkBlocks(X, Z)) {
			system_msg = "範囲指定が不適切です。時計回りまたは反時計回りに指定してください。";
		} else if (blocks != Math.floor(blocks)) {
			system_msg = "ブロック数が不適切(整数でない)です。範囲が正確に指定されていない可能性があります。";
		} else if (blocks >= 2500000) {
			system_msg = "拡張最大制限ブロック数(2,500,000ブロック)以上です。「自治体関連方針」により原則的に認可できません。";
		} else if (blocks >= 250000) {
			system_msg = "初期規定ブロック数(250,000ブロック)以上です。新規登録の場合は「規定ブロック数を超える明確な理由」が必要です。";
		} else if (Math.round(blocks) == 0) {
			system_msg = "範囲情報を入力してください。";
		}

		channel.sendMessage(user.getAsMention() + ", " + blocks + " Blocks (" + X.size() + ")\n"
				+ "メッセージ: `" + system_msg + "`\n"
				+ "\n"
				+ "範囲指定に誤りがあり、修正を行う場合は申請のメッセージを削除するなど、__**明確に申請の取り消し**__を行ってください。\n"
				+ "また、申請後に未だ認可がされてない状態で申請内容を変更したい場合、__**申請のメッセージを編集するのではなく再度申請をし直して**__ください。"
				+ "```" + debug + "```").queue();
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			channel.sendMessage(user.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。(`MySQLDBManager null`)")
					.queue();
			return;
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

				if (polygon.intersects(other_polygon.getBounds())) {
					ret_message += regName + "(" + regID + "|intersects1)";
					bool = false;
					continue;
				}

				if (polygon.contains(other_polygon.getBounds())) {
					ret_message += regName + "(" + regID + "|contains1)";
					bool = false;
					continue;
				}

				if (other_polygon.intersects(polygon.getBounds())) {
					ret_message += regName + "(" + regID + "|intersects2)";
					bool = false;
					continue;
				}

				if (other_polygon.contains(polygon.getBounds())) {
					ret_message += regName + "(" + regID + "|contains2)";
					bool = false;
					continue;
				}
			}
			if (!bool) {
				channel.sendMessage(user.getAsMention() + ", 次の自治体と範囲が重複している可能性があります。```" + ret_message + "```")
						.queue();
			}
			return;
		} catch (SQLException e) {
			channel.sendMessage(user.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。\n"
					+ "**Message**: `" + e.getMessage() + "`").queue();
			return;
		}
	}

	@Override
	public boolean isAlsoTargetEdited() {
		return false;
	}
}
