package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Potato implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        channel.sendMessage("(╮╯╭)").queue();
    }

    @Override
    public String getDescription() {
        return "「(╮╯╭)」を発言されたチャンネルに投稿します。";
    }

    @Override
    public String getUsage() {
        return "/potato";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}