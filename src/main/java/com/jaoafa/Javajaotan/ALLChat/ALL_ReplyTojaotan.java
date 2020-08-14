package com.jaoafa.Javajaotan.ALLChat;

import com.jaoafa.Javajaotan.ALLChatPremise;
import com.jaoafa.Javajaotan.Lib.ChatManager;
import com.jaoafa.Javajaotan.Main;
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

        String ret = getReplyMessage(user, content);
        if (ret == null) {
            channel.sendMessage(member.getAsMention() + ", 返信メッセージの取得に失敗しました。").queue();
            return;
        }
        channel.sendMessage(member.getAsMention() + ", " + ret).queue();
    }

    private String getReplyMessage(User user, String content) {
        ChatManager chatManager = Main.getChatManager();
        if (chatManager == null) {
            return null;
        }

        if (content.startsWith("!")) {
            System.out.println("chatA3RT content: " + content.substring(1).trim());
            String ret = chatManager.chatA3RT(content.substring(1).trim());
            if (ret == null)
                return null;
            return ret + " (A3RT [③])";
        } else if (content.startsWith(":")) {
            System.out.println("chatChatplus content: " + content.substring(1).trim());
            String ret = chatManager.chatChaplus(user, content.substring(1).trim());
            if (ret == null)
                return null;
            return ret + " (Chaplus [②])";
        } else if (content.startsWith(";")) {
            System.out.println("chatNoby content: " + content.substring(1).trim());
            String ret = chatManager.chatNoby(content.substring(1).trim());
            if (ret == null)
                return null;
            return ret + " (CotogotoNoby [④])";
        } else {
            System.out.println("content: " + content);
            String ret = chatManager.chatUserLocal(user, content);
            if (ret != null) {
                return ret + " (userLocal [①])";
            }

            ret = chatManager.chatChaplus(user, content);
            if (ret != null) {
                return ret + " (Chaplus [②])";
            }

            ret = chatManager.chatA3RT(content);
            if (ret != null) {
                return ret + " (A3RT [③])";
            }

            ret = chatManager.chatNoby(content);
            if (ret != null) {
                return ret + " (Noby [④])";
            }
        }
        return null;
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}