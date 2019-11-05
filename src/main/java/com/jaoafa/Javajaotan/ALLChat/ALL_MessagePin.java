package com.jaoafa.Javajaotan.ALLChat;

import java.util.stream.Collectors;

import com.jaoafa.Javajaotan.ALLChatPremise;
import com.jaoafa.Javajaotan.Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ALL_MessagePin implements ALLChatPremise {
	@Override
	public void run(JDA jda, Guild guild, MessageChannel channel, Member member, Message message,
			boolean edited) {
		String text = message.getContentRaw();

		if (text.startsWith("📌")) {
			if (edited && message.isPinned()) {
				message.addReaction("📌").queue();
				return;
			}
			message.pin().queue(null, failure -> {
				message.retrieveReactionUsers("❌").queue(success -> {
					boolean bool = success.stream()
							.filter(_user -> (_user != null && _user.getIdLong() == jda.getSelfUser().getIdLong()))
							.collect(Collectors.toList()).isEmpty();
					if (!bool) {
						return;
					}
					channel.sendMessage(
							member.getAsMention() + ", メッセージをピン止めするのに失敗しました。```" + failure.getMessage() + "```")
							.queue();
				});
			});
			message.addReaction("📌").queue(null, failure -> {
				Main.DiscordExceptionError(getClass(), channel, failure);
			});
		}
	}

	@Override
	public boolean isAlsoTargetEdited() {
		return true;
	}
}