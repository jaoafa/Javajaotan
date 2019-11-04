package com.jaoafa.Javajaotan.Command;

import java.util.Arrays;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

public class Cmd_Chat implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		Role[] allowRoles = {
				jda.getRoleById(189381504059572224L), // old jGC Admin
				jda.getRoleById(281699181410910230L), // old jGC Moderator
				jda.getRoleById(597405109290532864L), // new jGC Admin
				jda.getRoleById(597405110683041793L), // new jGC Moderator
		};
		MessageChannel sendToChannel = channel;
		if (Library.isAllowRole(member, allowRoles)) {
			// チャンネル指定可
			if (args.length >= 2) {
				if (Library.isLong(args[0])) {
					long channelID = Long.valueOf(args[0]);
					if (jda.getTextChannelById(channelID) == null) {
						channel.sendMessage(member.getAsMention() + ", 指定されたチャンネルが見つかりません。(ID指定)");
						return;
					}
					sendToChannel = jda.getTextChannelById(channelID);
				} else {
					if (jda.getTextChannelById(args[0]) == null) {
						channel.sendMessage(member.getAsMention() + ", 指定されたチャンネルが見つかりません。(チャンネル名指定)");
						return;
					}
				}
			}
		}
		String content;
		if (sendToChannel.getIdLong() == channel.getIdLong()) {
			// チャンネル指定なし
			content = String.join(" ", args);
		} else {
			// チャンネル指定あり
			content = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		}
		sendToChannel.sendMessage(content);
	}

	@Override
	public String getDescription() {
		return "指定されたチャンネルにチャットを送信します。運営未満の権限の利用者はチャンネルの指定ができません。";
	}

	@Override
	public String getUsage() {
		return "/chat [ChannelID] <Message...>";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}
}
