package com.jaoafa.Javajaotan.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jaoafa.Javajaotan.ChannelPremise;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class Channel_603841992404893707 implements ChannelPremise {
	// #greeting 603841992404893707
	private static List<Long> jaoPlayers = new ArrayList<Long>();

	@Override
	public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
			boolean edited) {
		if (message.getType() != MessageType.DEFAULT) {
			return;
		}
		if (!message.getContentRaw().equals("jao") && !message.getContentRaw().equals("afa")) {
			message.delete().queue();
			return;
		}
		Role role = jda.getRoleById(597421078817669121L);
		if (role == null) {
			channel.sendMessage("<@221991565567066112> ROLE IS NOT FOUND").queue();
			return;
		}
		if (message.getContentRaw().equals("jao")) {
			List<Role> roles = member.getRoles().stream().filter(_role -> _role.getIdLong() == role.getIdLong())
					.collect(Collectors.toList());
			if (roles.size() == 0) {
				message.addReaction("\u2753").queue(); // ?
				jaoPlayers.add(user.getIdLong());
			} else {
				message.addReaction("\u274C").queue(); // x
			}
		} else if (message.getContentRaw().equals("afa")) {
			if (!jaoPlayers.contains(user.getIdLong())) {
				message.addReaction("\u274C").queue(); // x
				return;
			}
			guild.addRoleToMember(member, role).queue();
			message.addReaction("\u2B55").queue(); // o
			channel.sendMessage(user.getAsMention() + ", あいさつしていただきありがとうございます！これにより、多くのチャンネルを閲覧できるようになりました。\n" +
					"このあとは<#597419057251090443>などで__**「`/link`」を実行(投稿)して、MinecraftアカウントとDiscordアカウントを連携**__しましょう！\n"
					+ "**<#706818240759988224>に記載されているメッセージもお読みください！**").queue();
			jaoPlayers.remove(user.getIdLong());
		}
	}

	@Override
	public boolean isAlsoTargetEdited() {
		return true;
	}
}