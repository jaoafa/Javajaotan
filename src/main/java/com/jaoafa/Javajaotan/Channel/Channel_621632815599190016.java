package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class Channel_621632815599190016 implements ChannelPremise {
    // #659
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getType() != MessageType.DEFAULT) {
            return;
        }
        message.reply(message.getTimeCreated().toLocalDateTime().toString().replace("T", " ")).queue();
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}
