package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Cmd_Tweet implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (!Library.hasAdminModeratorRole(guild, member)) {
            message.reply("あなたの権限ではこのコマンドは使用できません。").queue();
            return;
        }
        if (args.length == 0) {
            message.reply("" + getUsage()).queue();
            return;
        }
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            Status status = twitter.updateStatus(String.join(" ", args) + " #jaoafa");
            if (status == null) {
                message.reply("ツイートの送信に失敗しました: `status is null.`").queue();
                return;
            }
            message.reply("ツイートの送信に成功しました: https://twitter.com/"
                    + status.getUser().getScreenName() + "/status/" + status.getId()).queue();
        } catch (TwitterException e) {
            message.reply("ツイートの送信に失敗しました: `" + e.getMessage() + " (" + e.getErrorCode()
                    + " / " + e.getStatusCode() + ")`").queue();
        }
    }

    @Override
    public String getDescription() {
        return "@jaoafaアカウントでツイートします。";
    }

    @Override
    public String getUsage() {
        return "/tweet <Text>";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}
