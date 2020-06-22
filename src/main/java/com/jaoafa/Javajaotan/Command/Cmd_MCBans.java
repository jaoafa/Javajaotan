package com.jaoafa.Javajaotan.Command;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.MCBans;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_MCBans implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length == 0) {
			channel.sendMessage(member.getAsMention() + ", 引数が足りません。").queue();
			return;
		}
		MCBans mcbans = null;
		try {
			mcbans = new MCBans(args[0]);
		} catch (IOException e) {
			channel.sendMessage(member.getAsMention() + ", 取得に失敗しました。(IOException : " + e.getMessage() + ")").queue();
			return;
		}

		if (!mcbans.isFound()) {
			channel.sendMessage(member.getAsMention() + ", 取得に失敗しました。該当するプレイヤー情報が見つかりません。").queue();
			return;
		}

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("MCBans DATA : " + mcbans.getMinecraftID(),
				"https://www.mcbans.com/player/" + mcbans.getUUID().toString().replace("-", "") + "/");
		embed.setColor(Color.RED);
		embed.setDescription("Global: " + mcbans.getGlobalCount() + " / Local: " + mcbans.getLocalCount());
		embed.setTimestamp(Instant.now());

		int[] global_ids = mcbans.getGlobalBanIds();
		int[] local_ids = mcbans.getLocalBanIds();
		for (int id : global_ids) {
			try {
				MCBans.Ban ban = new MCBans.Ban(id);
				String reason = ban.getReason();
				String date = ban.getDate();
				String banned_by = ban.getBannedBy();
				String server = ban.getServer();

				embed.addField("[Global] `" + server + "`",
						"Reason: " + reason + "\nBanned_by: `" + banned_by + "`\n" + date, false);
			} catch (IOException e) {
				embed.addField("BanID: " + id, "Failed to get the data", false);
			}
		}
		for (int id : local_ids) {
			try {
				MCBans.Ban ban = new MCBans.Ban(id);
				String reason = ban.getReason();
				String date = ban.getDate();
				String banned_by = ban.getBannedBy();
				String server = ban.getServer();

				embed.addField("[Local] `" + server + "`",
						"Reason: " + reason + "\nBanned_by: `" + banned_by + "`\n" + date, false);
			} catch (IOException e) {
				embed.addField("BanID: " + id, "Failed to get the data", false);
			}
		}

		channel.sendMessage(embed.build()).queue();
	}

	@Override
	public String getDescription() {
		return "MCBansからプレイヤーの情報を取得し返却します。";
	}

	@Override
	public String getUsage() {
		return "/mcbans <MinecraftID/UUID>";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}
}