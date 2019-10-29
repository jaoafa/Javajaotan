package com.jaoafa.Javajaotan.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Cmd_Origin implements CommandPremise {
	Pattern title_pattern = Pattern.compile("<TD nowarp><FONT.+>(.+?)</FONT>");
	Pattern text_pattern = Pattern.compile("<p.*?>([\\s\\S]+?)</p>");

	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length != 1) {
			channel.sendMessage("このコマンドを実行するには、1つの引数が必要です。").queue();
			return;
		}
		String num = args[0];
		if (!NumberUtils.isDigits(num)) {
			channel.sendMessage("数値を指定してください。").queue();
			return;
		}
		try {

			Path path = Paths.get("/var/jaoafa/discord/kinenbi.json");
			List<String> datas = Files.readAllLines(path);
			JSONObject json = new JSONObject(String.join("\n", datas));
			if (!json.has(num)) {
				channel.sendMessage("指定された記念日ナンバーの記念日が見つかりませんでした。").queue();
				return;
			}
			String url = json.getString(num);
			OkHttpClient okclient = new OkHttpClient();
			Request request = new Request.Builder().url(url).build();
			Response response = okclient.newCall(request).execute();
			String res = response.body().string();
			Matcher title_matcher = title_pattern.matcher(res);
			if (!title_matcher.find()) {
				channel.sendMessage("指定された記念日ナンバーの記念日の情報を取得できませんでした。(title|`" + url + "`)").queue();
				return;
			}
			String title = title_matcher.group(1);
			Matcher text_matcher = text_pattern.matcher(res);
			if (!text_matcher.find()) {
				channel.sendMessage("指定された記念日ナンバーの記念日の情報を取得できませんでした。(text|`" + url + "`)").queue();
				return;
			}
			String text = text_matcher.group(1);
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