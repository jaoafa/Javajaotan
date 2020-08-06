package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Discordlink implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        channel.sendMessage(member.getAsMention()
                + ", discordlinkコマンドはDiscordではなくjao Minecraft ServerのMinecraftサーバ内で実行してね！そうしないとあなたのMinecraftアカウントがわからないからね！")
                .queue();
    }

    @Override
    public String getDescription() {
        return "ダミーコマンドです。このコマンドはMinecraftサーバ内で打ち込んでください。";
    }

    @Override
    public String getUsage() {
        return "/discordlink";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}