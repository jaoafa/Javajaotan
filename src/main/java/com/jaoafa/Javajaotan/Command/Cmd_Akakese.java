package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cmd_Akakese implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        String replyId = "<@222018383556771840>"; // jaotan
        Pattern all_id_pattern = Pattern.compile("^[0-9]+$");
        if (args.length == 0) {
            replyId = member.getAsMention();
        } else if (args.length == 1) {
            Matcher all_id_matcher = all_id_pattern.matcher(args[0]);
            if (all_id_matcher.matches()) {
                replyId = "<@" + args[0] + ">";
            } else {
                replyId = args[0];
            }
        }
        channel.sendMessage(replyId + ", なンだおまえ!!!!帰れこのやろう!!!!!!!!人間の分際で!!!!!!!!寄るな触るな近づくな!!!!!!!!垢消せ!!!!垢消せ!!!!!!!! ┗(‘o’≡’o’)┛!!!!!!!!!!!!!!!! https://twitter.com/settings/accounts/confirm_deactivation").queue();
    }

    @Override
    public String getDescription() {
        return "垢消せ";
    }

    @Override
    public String getUsage() {
        return "/akakese";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}
