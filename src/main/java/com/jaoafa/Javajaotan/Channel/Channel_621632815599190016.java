package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Channel_621632815599190016 implements ChannelPremise {
    // #659
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getType() != MessageType.DEFAULT) {
            return;
        }
        message.reply(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(message.getTimeCreated().withOffsetSameInstant(ZoneOffset.ofHours(9)))).queue();
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}
