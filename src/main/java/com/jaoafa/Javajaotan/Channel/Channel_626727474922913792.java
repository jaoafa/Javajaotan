package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class Channel_626727474922913792 implements ChannelPremise {
    // #develop_todo
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getType() != MessageType.DEFAULT) {
            return;
        }
        message.pin().queue(success -> message.addReaction("🆕").queue(), failure -> {
            message.addReaction("❌").queue();
            message.reply("ピンエラー: `" + failure.getClass().getName() + " | " + failure.getMessage() + "`").queue();
        });
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}
