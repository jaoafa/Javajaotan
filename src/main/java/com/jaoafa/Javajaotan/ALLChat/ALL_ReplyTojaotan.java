package com.jaoafa.Javajaotan.ALLChat;

import com.jaoafa.Javajaotan.ALLChatPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.ChatManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;

public class ALL_ReplyTojaotan implements ALLChatPremise {
	@Override
	public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
			boolean edited) {
		if (message.getType() != MessageType.DEFAULT) {
			return;
		}
		if (message.getMentionedUsers() == null || message.getMentionedUsers().isEmpty()) {
			return;
		}
		boolean isReplyTojaotan = message.getMentionedUsers().stream()
				.filter(_user -> _user != null && _user.getIdLong() == jda.getSelfUser().getIdLong()).count() == 1;
		if (!isReplyTojaotan) {
			return;
		}

		String content = message.getContentRaw();
		content = content.replaceAll("<@!?[0-9]+>", "");
		content = content.trim();

		String ret = getReplyMessage(user, content);
		if (ret == null) {
			channel.sendMessage(member.getAsMention() + ", 返信メッセージの取得に失敗しました。").queue();
			return;
		}
		channel.sendMessage(member.getAsMention() + ", " + ret).queue();
	}

	private String getReplyMessage(User user, String content) {
		ChatManager chatManager = Main.getChatManager();
		if (chatManager == null) {
			return null;
		}

		System.out.println("content: " + content);

		if (content.startsWith("!")) {
			String ret = chatManager.chatA3RT(content);
			if (ret == null)
				return null;
			return ret + " (A3RT [②])";
		} else if (content.startsWith(";")) {
			String ret = chatManager.chatNoby(content);
			if (ret == null)
				return null;
			return ret + " (CotogotoNoby [④])";
		} else {
			String ret = chatManager.chatUserLocal(user, content);
			if (ret != null) {
				return ret + " (userLocal [①])";
			}

			ret = chatManager.chatA3RT(content);
			if (ret != null) {
				return ret + " (A3RT [②])";
			}

			ret = chatManager.chatNoby(content);
			if (ret != null) {
				return ret + " (Noby [④])";
			}
		}
		return null;
	}

	@Override
	public boolean isAlsoTargetEdited() {
		return false;
	}
}