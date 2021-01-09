package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import com.jaoafa.Javajaotan.Lib.ChatManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class Channel_745979710898438275 implements ChannelPremise {
    // #jaochat
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getType() != MessageType.DEFAULT) {
            return;
        }
        String content = message.getContentRaw();
        String ret = ChatManager.getReplyMessage(user, content);
        if (ret == null) {
            message.reply("返信メッセージの取得に失敗しました。").queue();
            return;
        }
        System.out.println("[jaoChat] " + content + " -> " + ret);
        message.reply(ret).queue();
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}
