package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class Channel_597423370589700098 implements ChannelPremise {
    // #support
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getType() != MessageType.DEFAULT) {
            return;
        }
        Role role = jda.getRoleById(786110419470254102L);
        if (role == null) {
            channel.sendMessage("<@221991565567066112> ROLE IS NOT FOUND").queue();
            return;
        }
        boolean isNeedSupport = member.getRoles().stream().anyMatch(_role -> _role.getIdLong() == role.getIdLong());
        if (isNeedSupport) {
            return;
        }
        guild.addRoleToMember(member, role).queue();
        message.addReaction("\uD83D\uDC40").queue(); // :eyes:
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}
