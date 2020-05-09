package com.jaoafa.Javajaotan;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public interface ALLChatPremise {
	/**
	 * チャンネルでメッセージが送信されたときに呼び出します。
	 * @param client IDiscordClient
	 * @param guild 送信元のGuild(Discord Server)
	 * @param channel 送信元のチャンネル
	 * @param author 送信者(実行者)
	 * @param message メッセージに関するデータ
	 * @param edited 編集による呼び出しかどうか
	 */
	public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
			boolean edited);

	/**
	 * 編集された場合でも呼び出すかを指定・確認します。
	 * @return 編集された場合でも呼び出すか
	 */
	public boolean isAlsoTargetEdited();
}
