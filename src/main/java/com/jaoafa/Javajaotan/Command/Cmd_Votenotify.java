package com.jaoafa.Javajaotan.Command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Votenotify implements CommandPremise {
	static Set<String> mcjp_strings = new HashSet<>();
	static Set<String> mono_strings = new HashSet<>();

	static {
		mcjp_strings.add("mcjp");
		mcjp_strings.add("minecraft.jp");

		mono_strings.add("mono");
		mono_strings.add("monocraft");
		mono_strings.add("monocraft.net");
	}

	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		// サービス選択 -> タイプ選択(毎日無条件・前日投票の場合のみ・オフ) -> 何時にチェックか -> 保存

		File file = new File("votenotify.json");
		JSONObject mcjp = new JSONObject();
		JSONObject mono = new JSONObject();
		if (file.exists()) {
			try {
				String source = String.join("\n", Files.readAllLines(file.toPath()));
				JSONObject json = new JSONObject(source);
				if (json.has("mcjp"))
					mcjp = json.getJSONObject("mcjp");
				if (json.has("mono"))
					mono = json.getJSONObject("mono");
			} catch (IOException e) {
			}
		}

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("VoteNotify");
		embed.setDescription(member.getAsMention());
		if (args.length == 0) {
			// 現在の設定表示

			// mcjp
			if (!mcjp.has(member.getId())) {
				// ない
				embed.addField("minecraft.jp : 現在の設定", "オフ", false);
				channel.sendMessage(embed.build()).queue();
				return;
			} else {
				JSONObject obj = mcjp.getJSONObject(member.getId());
				if (obj.getString("type").equals("everyday")) {
					// 毎日無条件
					embed.addField("minecraft.jp : 現在の設定", "オン (毎日通知)", false);
				} else if (obj.getString("type").equals("before")) {
					// 前日に投票した場合のみ
					embed.addField("minecraft.jp : 現在の設定", "オン (前日に投票した場合のみ)", false);
				} else {
					embed.addField("minecraft.jp : 現在の設定", "オン (" + obj.getString("type") + ")", false);
				}
				embed.addField("minecraft.jp : 通知時刻", String.format("%02d", obj.getInt("time")) + "時", false);
			}

			// mono
			if (!mono.has(member.getId())) {
				// ない
				embed.addField("monocraft.net : 現在の設定", "オフ", false);
				channel.sendMessage(embed.build()).queue();
				return;
			} else {
				JSONObject obj = mono.getJSONObject(member.getId());
				if (obj.getString("type").equals("everyday")) {
					// 毎日無条件
					embed.addField("monocraft.net : 現在の設定", "オン (毎日通知)", false);
				} else if (obj.getString("type").equals("before")) {
					// 前日に投票した場合のみ
					embed.addField("monocraft.net : 現在の設定", "オン (前日に投票した場合のみ)", false);
				} else {
					embed.addField("monocraft.net : 現在の設定", "オン (" + obj.getString("type") + ")", false);
				}
				embed.addField("monocraft.net : 通知時刻", String.format("%02d", obj.getInt("time")) + "時", false);
			}

			channel.sendMessage(embed.build()).queue();
			return;
		} else if (args.length == 2) {
			// /votenotify mcjp <everyday, before, off>
			if (!args[1].equalsIgnoreCase("off")) {
				channel.sendMessage(
						member.getAsMention() + ", `off`以外を指定する場合は時間を指定してください。\n"
								+ "例: `/votenotify mcjp everyday 00`")
						.queue();
				return;
			}
			if (mcjp_strings.contains(args[0])) {
				// mcjp -> off
				if (mcjp.has(member.getId())) {
					mcjp.remove(member.getId());
				}
				save(file, mcjp, mono);
				channel.sendMessage(member.getAsMention() + ", minecraft.jpの投票お知らせを無効化しました。").queue();
				return;
			} else if (mono_strings.contains(args[0])) {
				// mono -> off
				if (mono.has(member.getId())) {
					mono.remove(member.getId());
				}
				save(file, mcjp, mono);
				channel.sendMessage(member.getAsMention() + ", monocraft.netの投票お知らせを無効化しました。").queue();
				return;
			} else {
				channel.sendMessage(member.getAsMention() + ", 第1引数には`" + String.join(", ", mcjp_strings) + "`, `"
						+ String.join(", ", mono_strings) + "`のいずれかを指定してください。\n"
						+ "例: `/votenotify mcjp everyday 00`").queue();
				return;
			}
		} else if (args.length == 3) {
			// /votenotify mcjp <everyday, before, off> <Hour>
			if (!args[1].equalsIgnoreCase("everyday") && !args[1].equalsIgnoreCase("before")) {
				channel.sendMessage(
						member.getAsMention() + ", 第2引数には`everyday`, `before`, `off`のみを指定できます。`off`を指定する場合は第3引数は不要です。\n"
								+ "例: `/votenotify mcjp everyday 00`\n"
								+ "例: `/votenotify mcjp off`")
						.queue();
				return;
			}
			if (!Library.isInt(args[2])) {
				channel.sendMessage(member.getAsMention() + ", 第3引数には数値を指定してください。").queue();
				return;
			}
			int hour = Integer.parseInt(args[2]);
			if (hour < 0 || hour > 23) {
				channel.sendMessage(member.getAsMention() + ", 第3引数には00～23を指定してください。").queue();
				return;
			}
			String type = args[1].equalsIgnoreCase("everyday") ? "everyday" : "before";
			String type_ja = args[1].equalsIgnoreCase("everyday") ? "毎日通知" : "前日に投票した場合のみ";
			if (mcjp_strings.contains(args[0])) {
				// mcjp -> everyday or before
				JSONObject obj = new JSONObject();
				obj.put("type", type);
				obj.put("time", hour);
				mcjp.put(member.getId(), obj);
				save(file, mcjp, mono);
				channel.sendMessage(member.getAsMention() + ", minecraft.jpの投票お知らせを「" + type_ja + "」・「"
						+ String.format("%02d", hour) + "時」に設定しました。").queue();
				return;
			} else if (mono_strings.contains(args[0])) {
				// mono -> everyday or before
				JSONObject obj = new JSONObject();
				obj.put("type", type);
				obj.put("time", hour);
				mono.put(member.getId(), obj);
				save(file, mcjp, mono);
				channel.sendMessage(member.getAsMention() + ", monocraft.netの投票お知らせを「" + type_ja + "」・「"
						+ String.format("%02d", hour) + "時」に設定しました。").queue();
				return;
			} else {
				channel.sendMessage(member.getAsMention() + ", 第1引数には`" + String.join(", ", mcjp_strings) + "`, `"
						+ String.join(", ", mono_strings) + "`のいずれかを指定してください。\n"
						+ "例: `/votenotify mcjp everyday 00`").queue();
				return;
			}
		}
	}

	private void save(File file, JSONObject mcjp, JSONObject mono) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("mcjp", mcjp);
			obj.put("mono", mono);
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.write(obj.toString());
			pw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "未投票時に通知する機能の設定を行えます。";
	}

	@Override
	public String getUsage() {
		return "/votenotify <mcjp, mono> <everyday, before, off> <Hour>";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}
}
