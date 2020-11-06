package com.jaoafa.Javajaotan.ALLChat;

import com.jaoafa.Javajaotan.ALLChatPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class ALL_Bakushinchi implements ALLChatPremise {
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        String text = message.getContentRaw();
        if (guild.getIdLong() != 597378876556967936L) {
            return;
        }
        if (!text.contains("爆心地")) {
            return;
        }
        message.delete().queue();
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return true;
    }
}