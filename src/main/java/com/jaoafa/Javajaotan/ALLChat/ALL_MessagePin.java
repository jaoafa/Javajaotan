package com.jaoafa.Javajaotan.ALLChat;

import com.jaoafa.Javajaotan.ALLChatPremise;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class ALL_MessagePin implements ALLChatPremise {
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        String text = message.getContentRaw();

        if (text.startsWith("📌")) {
            if (edited && message.isPinned()) {
                message.addReaction("📌").queue();
                return;
            }
            message.retrieveReactionUsers("📌").queue(users -> {
                long isjaotanPinned = users.stream()
                        .filter(_user -> (_user != null && _user.getIdLong() == 222018383556771840L)).count();
                if (isjaotanPinned == 0) {
                    message.pin().queue(
                        s -> System.out.println("[ALL_MessagePin] Pinned. "),
                        failure -> message.retrieveReactionUsers("❌").queue(
                            success -> {
                                boolean bool = success.stream().noneMatch(_user -> (_user != null && _user.getIdLong() == jda.getSelfUser().getIdLong()));
                                if (!bool) {
                                    return;
                                }
                                message.reply("メッセージをピン止めするのに失敗しました。```" + failure.getMessage() + "```")
                                    .queue();
                            }
                        )
                    );
                }
            });

            message.addReaction("📌").queue(null, failure -> Main.DiscordExceptionError(getClass(), message, failure));
        }
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return true;
    }
}