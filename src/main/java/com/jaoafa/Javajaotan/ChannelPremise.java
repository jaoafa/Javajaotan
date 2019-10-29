package com.jaoafa.Javajaotan;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public interface ChannelPremise {
	/**
	 * チャンネルでメッセージが送信されたときに呼び出します。
	 * @param jda JDA
	 * @param guild 送信元のGuild(Discord Server)
	 * @param channel 送信元のチャンネル
	 * @param member 送信者(実行者)
	 * @param message メッセージに関するデータ
	 * @param edited 編集による呼び出しかどうか
	 */
	public void run(final JDA jda, final Guild guild, final MessageChannel channel, final Member member,
			final Message message, boolean edited);

	/**
	 * 編集された場合でも呼び出すかを指定・確認します。
	 * @return 編集された場合でも呼び出すか
	 */
	public boolean isAlsoTargetEdited();
}
