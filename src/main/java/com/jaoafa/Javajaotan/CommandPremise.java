package com.jaoafa.Javajaotan;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public interface CommandPremise {
    /**
     * コマンドが実行されたときに呼び出します。
     *
     * @param jda     JDA
     * @param guild   送信元のGuild(Discord Server)
     * @param channel 送信元のチャンネル
     * @param member  送信者(実行者)・Guildに紐ついたユーザーデータ
     * @param message メッセージに関するデータ
     * @param args    引数(コマンド自体を除く)
     */
    void onCommand(final JDA jda, final Guild guild, final MessageChannel channel, final Member member,
                   final Message message, final String[] args);

    /**
     * コマンドを説明する文章を指定・返却します
     *
     * @return コマンドを説明する文章
     */
    String getDescription();

    /**
     * コマンドの使い方を指定・返却します。
     *
     * @return コマンドの使い方
     */
    String getUsage();

    /**
     * jMS Gamers Clubのみで使用できるコマンドかどうかを返却します。
     *
     * @return jMS Gamers Clubのみであればtrue
     */
    boolean isjMSOnly();
}
