package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class Channel_597424023621599232 implements ChannelPremise {
    // #todo
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getType() != MessageType.DEFAULT) {
            return;
        }
        message.pin().queue(success -> message.addReaction("🆕").queue(), failure -> {
            message.addReaction("❌").queue();
            channel.sendMessage("ピンエラー: `" + failure.getClass().getName() + " | " + failure.getMessage() + "`").queue();
        });
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}
