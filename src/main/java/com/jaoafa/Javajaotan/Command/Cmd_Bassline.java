package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cmd_Bassline implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        String replyId = "<@222018383556771840>"; // jaotan
        Pattern all_id_pattern = Pattern.compile("^[0-9]+$");
        if (args.length == 0) {
            // Hiratake Nanami
            replyId = "<@221498004505362433>"; // hiratake
        } else if (args.length == 1) {
            Matcher all_id_matcher = all_id_pattern.matcher(args[0]);
            if (all_id_matcher.matches()) {
                replyId = "<@" + args[0] + ">";
            } else {
                replyId = args[0];
            }
        }
        channel.sendMessage("ベースラインパーティーの途中ですが、ここで臨時ニュースをお伝えします。\n"
                + "今日昼頃、わりとキモく女性にナンパをしたうえ、路上で爆睡をしたとして、\n"
                + "道の上で寝たり、女の子に声をかけたりしたらいけないんだよ罪の容疑で、\n"
                + "自称優良物件、" + replyId + "容疑者が逮捕されました。").queue();
    }

    @Override
    public String getDescription() {
        return "ベースラインパーティーの途中の臨時ニュースを発言されたチャンネルに投稿します。\nhttps://youtu.be/55AalrbALAk";
    }

    @Override
    public String getUsage() {
        return "/bassline [UserID|Text]";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}
