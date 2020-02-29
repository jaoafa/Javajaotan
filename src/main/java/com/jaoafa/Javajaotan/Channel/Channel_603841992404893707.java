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

public class Channel_603841992404893707 implements ChannelPremise {
	// #greeting 603841992404893707
	private static List<Long> jaoPlayers = new ArrayList<Long>();

	@Override
	public void run(JDA jda, Guild guild, MessageChannel channel, Member member, Message message,
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
				guild.addRoleToMember(member, role).queue();
				message.addReaction("\u003F").queue(); // ?
				jaoPlayers.add(member.getUser().getIdLong());
			} else {
				message.addReaction("\u274C").queue(); // x
			}
		} else if (message.getContentRaw().equals("afa")) {
			if (!jaoPlayers.contains(member.getUser().getIdLong())) {
				message.addReaction("\u274C").queue(); // x
				return;
			}
			message.addReaction("\u2B55").queue(); // o
			channel.sendMessage(member.getAsMention() + ", あいさつしていただきありがとうございます！これにより、多くのチャンネルを閲覧できるようになりました。\n" +
					"このあとは<#597419057251090443>などで「`/link`」を実行(投稿)して、MinecraftアカウントとDiscordアカウントを連携しましょう！").queue();
			jaoPlayers.remove(member.getUser().getIdLong());
		}
	}

	@Override
	public boolean isAlsoTargetEdited() {
		return true;
	}
}