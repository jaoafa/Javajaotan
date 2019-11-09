package com.jaoafa.Javajaotan.Command;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.json.JSONArray;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.Library;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Cmd_Tokanji implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length == 0) {
			channel.sendMessage(member.getAsMention() + ", " + getUsage()).queue();
			return;
		}
		String original;
		int readNum = 0;
		if (args.length >= 2 && Library.isInt(args[0]) && Integer.parseInt(args[0]) > 0) {
			original = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			readNum = Integer.parseInt(args[0]);
		} else {
			original = String.join(" ", args);
		}

		try {
			original = URLEncoder.encode(original, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Main.ExceptionReporter(channel, e);
			return;
		}

		String url = "http://www.google.com/transliterate?langpair=ja-Hira|ja&text=" + original;
		try {
			OkHttpClient okclient = new OkHttpClient();
			Request request = new Request.Builder().url(url).build();
			Response response = okclient.newCall(request).execute();
			String res = response.body().string();
			response.close();
			JSONArray array = new JSONArray(res);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < array.length(); i++) {
				JSONArray predictions = array.getJSONArray(i).getJSONArray(1);
				if (predictions.length() < readNum) {
					builder.append(array.getJSONArray(i).getString(0));
				} else {
					builder.append(predictions.getString(readNum));
				}
			}

			channel.sendMessage(member.getAsMention() + ", ```" + builder.toString() + "```").queue();
		} catch (IOException e) {
			Main.ExceptionReporter(channel, e);
			return;
		}
	}

	@Override
	public String getDescription() {
		return "指定されたテキストを漢字に直します。readNumを指定すると、指定された候補番号の候補漢字(見つからなければ仮名)で構成します。「Google CGI API for Japanese Input」を使用しています。 https://www.google.co.jp/ime/cgiapi.html";
	}

	@Override
	public String getUsage() {
		return "/tokanji [readNum] <Text>";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}
