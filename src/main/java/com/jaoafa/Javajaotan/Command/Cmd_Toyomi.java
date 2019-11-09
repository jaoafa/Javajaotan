package com.jaoafa.Javajaotan.Command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Toyomi implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		String original = String.join(" ", args);
		String json = getRunCommand(message, "php", "/var/jaoafa/Javajaotan/extcmds/toyomi.php", original);
		JSONObject obj = new JSONObject(json);
		channel.sendMessage(member.getAsMention() + ", ```" + obj.optString("output", "null") + "```").queue();
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
		return "指定されたテキストの読みを返します。";
	}

	@Override
	public String getUsage() {
		return "/toyomi <Text>";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}