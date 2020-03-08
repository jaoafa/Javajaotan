package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Mcserver implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		return;
		/*
		if (channel.getIdLong() != 597423444501463040L) {
			channel.sendMessage(member.getAsMention() + ", 実行しようとしたコマンドはこのチャンネルでは使用できません。").queue();
			return;
		}
		
		channel.sendMessage(
				member.getAsMention() + ", 現在この機能は動作サーバの違いにより無効化されています。Minecraftサーバ停止・再起動は管理部までお問合せください。").queue();
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("start")) {
				runCommand(message, "systemctl", "start", "minecraft");
				return;
			} else if (args[0].equalsIgnoreCase("stop")) {
				runCommand(message, "systemctl", "stop", "minecraft");
				return;
			} else if (args[0].equalsIgnoreCase("restart")) {
				runCommand(message, "systemctl", "restart", "minecraft");
				return;
			} else if (args[0].equalsIgnoreCase("kill")) {
				runCommand(message, "systemctl", "kill", "minecraft");
				return;
			} else if (args[0].equalsIgnoreCase("status")) {
				//runCommand(message, "systemctl", "status", "minecraft");
				String result = getRunCommand(message, "php", "/var/jaoafa/Javajaotan/extcmds/mcstatus.php");
				JSONObject json = new JSONObject(result);
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("Minecraft Server Status");
				builder.appendDescription(
						member.getAsMention() + ", 必要に応じて、`/mcserver restart` または `/mcserver kill`を実行してください。");
				builder.addField("server_isrunning", json.optString("server_isrunning", "null"), false);
				builder.addField("systemctl_state", json.optString("systemctl_state", "null"), false);
				builder.addField("pid", json.optString("pid", "null"), false);
				builder.addField("name", json.optString("name", "null"), false);
				channel.sendMessage(builder.build()).queue();
				return;
			} else if (args[0].equalsIgnoreCase("uptime")) {
				runCommand(message, "uptime");
				return;
			} else if (args[0].equalsIgnoreCase("tps")) {
				String command = "tps";
				runCommand(message, "php", "/var/jaoafa/Javajaotan/extcmds/mccmd.php", command);
				return;
			}
		} else if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("say")) {
				String command = "say " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				runCommand(message, "php", "/var/jaoafa/Javajaotan/extcmds/mccmd.php", command);
				return;
			} else if (args[0].equalsIgnoreCase("chat")) {
				String command = "chat " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				runCommand(message, "php", "/var/jaoafa/Javajaotan/extcmds/mccmd.php", command);
				return;
			} else if (args[0].equalsIgnoreCase("tell")) {
				String command = "tell " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				runCommand(message, "php", "/var/jaoafa/Javajaotan/extcmds/mccmd.php", command);
				return;
			}
		}
		channel.sendMessage(member.getAsMention() + ", `" + getUsage() + "`").queue();
		*/
	}
	/*
	private void runCommand(Message message, String... command) {
		MessageChannel channel = message.getChannel();
		Process p;
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(command);
			builder.directory(new File("/var/1.12.2_jaoafaS3/"));
			builder.redirectErrorStream(true);
			p = builder.start();
			p.waitFor(10, TimeUnit.MINUTES);
		} catch (IOException e) {
			Main.ExceptionReporter(channel, e);
			return;
		} catch (InterruptedException e) {
			Main.ExceptionReporter(channel, e);
			return;
		}
		int ret = p.exitValue();
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
			return;
		}
		String last = text;
		channel.sendMessage(message.getAuthor().getAsMention() + ", ```" + last + "```(" + ret + ")")
				.queue(
						null,
						failure -> {
							System.out.println(
									"```" + last + "```(" + ret + ")\n\n\n"
											+ ("```" + last + "```(" + ret + ")").length()
											+ "\n");
							Main.DiscordExceptionError(getClass(), channel, failure);
						});
	}
	
	private String getRunCommand(Message message, String... command) {
		MessageChannel channel = message.getChannel();
		Process p;
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(command);
			builder.directory(new File("/var/1.12.2_jaoafaS3/"));
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
	*/

	@Override
	public String getDescription() {
		return "Minecraftサーバに関する操作を行います。特定のチャンネルでのみ使用できます。";
	}

	@Override
	public String getUsage() {
		return "/mcserver <start,stop,restart,kill,status,uptime,tps,say,chat,tell> [Value]";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}
}