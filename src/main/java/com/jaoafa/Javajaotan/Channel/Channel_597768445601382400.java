package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Message.Attachment;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class Channel_597768445601382400 implements ChannelPremise {
    // #nsfw
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getAttachments().size() == 0) {
            return;
        }
        for (Attachment attachment : message.getAttachments()) {
            try {
                URL url = new URL(attachment.getUrl());
                String fileName = Paths.get(url.getPath()).getFileName().toString();
                if (fileName.startsWith("SPOILER_")) {
                    continue;
                }
            } catch (MalformedURLException e) {
                continue;
            }
            message.reply("スポイラーの設定がされていないファイルは投稿できません。").queue();
            message.delete().queue();
            return;
        }
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return true;
    }
}