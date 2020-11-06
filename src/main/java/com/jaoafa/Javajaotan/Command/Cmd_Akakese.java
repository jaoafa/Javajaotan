package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Akakese implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (Library.isDenyToyCmd(channel)) {
            channel.sendMessage(member.getAsMention() + ", このチャンネルではこのコマンドを利用できません。<#616995424154157080>などで実行してください。").queue();
            return;
        }
        channel.sendMessage(member.getAsMention() + ", なンだおまえ!!!!帰れこのやろう!!!!!!!!人間の分際で!!!!!!!!寄るな触るな近づくな!!!!!!!!垢消せ!!!!垢消せ!!!!!!!! ┗(‘o’≡’o’)┛!!!!!!!!!!!!!!!! https://twitter.com/settings/accounts/confirm_deactivation").queue();
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
