package com.jaoafa.Javajaotan.ALLChat;

import com.jaoafa.Javajaotan.ALLChatPremise;
import com.jaoafa.Javajaotan.Lib.ChatManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class ALL_ReplyTojaotan implements ALLChatPremise {
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getType() != MessageType.DEFAULT) {
            return;
        }
        if (message.getMentionedUsers().isEmpty()) {
            return;
        }
        boolean isReplyTojaotan = message.getMentionedUsers().stream()
                .filter(_user -> _user != null && _user.getIdLong() == jda.getSelfUser().getIdLong()).count() == 1;
        if (!isReplyTojaotan) {
            return;
        }

        String content = message.getContentRaw();
        content = content.replaceAll("<@!?[0-9]+>", "");
        content = content.trim();

        String ret = ChatManager.getReplyMessage(user, content);
        if (ret == null) {
            message.reply("返信メッセージの取得に失敗しました。").queue();
            return;
        }
        System.out.println("[ReplyTojaotan] " + content + " -> " + ret);
        message.reply(ret).queue();
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}