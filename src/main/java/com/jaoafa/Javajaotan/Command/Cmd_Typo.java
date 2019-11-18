package com.jaoafa.Javajaotan.Command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Typo implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length == 0) {
			channel.sendMessage(member.getAsMention() + ", " + getUsage()).queue();
			return;
		}
		String original = String.join(" ", args);
		String json = getRunCommand(message, "php", "/var/jaoafa/Javajaotan/extcmds/typo.php", original);
		JSONArray array = new JSONArray(json);
		LinkedList<String> words = new LinkedList<>();
		for (int i = 0; i < array.length(); i++) {
			String word = array.getString(i);
			if (word.length() < 4) {
				words.add(word);
				continue;
			}
			String content = word.substring(1, word.length() - 1);
			List<String> split = Arrays.asList(content.split(""));
			Collections.shuffle(split);
			word = word.substring(0, 1) + String.join("", split) + word.substring(word.length() - 1, word.length());
			words.add(word);
		}
		channel.sendMessage(member.getAsMention() + ", ```" + String.join(" ", words) + "```").queue();
	}

	private String getRunCommand(Message message, String... command) {
		MessageChannel channel = message.getChannel();
		Process p;
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(command);
			builder.directory(new File("/var/jaoafa/Javajaotan/"));
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
		return "Typoglycemiaをします。";
	}

	@Override
	public String getUsage() {
		return "/typo <Text>";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}