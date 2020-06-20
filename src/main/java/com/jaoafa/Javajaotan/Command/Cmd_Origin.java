package com.jaoafa.Javajaotan.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Origin implements CommandPremise {
	Pattern title_pattern = Pattern.compile("<TD nowarp><FONT.+>(.+?)</FONT>");
	Pattern text_pattern = Pattern.compile("<p.*?>([\\s\\S]+?)</p>");

	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length != 1) {
			channel.sendMessage(member.getAsMention() + ", このコマンドを実行するには、1つの引数が必要です。").queue();
			return;
		}
		String num = args[0];
		if (!NumberUtils.isDigits(num)) {
			channel.sendMessage(member.getAsMention() + ", 数値を指定してください。").queue();
			return;
		}
		try {

			Path path = Paths.get("/var/jaoafa/discord/kinenbi.json");
			List<String> datas = Files.readAllLines(path);
			JSONObject json = new JSONObject(String.join("\n", datas));
			if (!json.has(num)) {
				channel.sendMessage(member.getAsMention() + ", 指定された記念日ナンバーの記念日が見つかりませんでした。").queue();
				return;
			}
			JSONObject obj = json.getJSONObject(num);
			String title = obj.getString("title");
			String text = obj.getString("text");
			channel.sendMessage(title + "```" + text + "```").queue();
			return;
		} catch (IOException e) {
			Main.ExceptionReporter(channel, e);
		} catch (JSONException e) {
			Main.ExceptionReporter(channel, e);
		}
	}

	@Override
	public String getDescription() {
		return "今日の記念日情報を詳しく表示します。";
	}

	@Override
	public String getUsage() {
		return "/origin <AnniversaryNumber>";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}

}