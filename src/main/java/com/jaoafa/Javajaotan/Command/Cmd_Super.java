package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Super implements CommandPremise {

    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member, Message message, String[] args) {
        channel.sendMessage(
                "ｽｩ( ᐛ👐) パァwﾍｸｻｺﾞｫﾝwwﾋﾞｷﾞｨﾝwﾃﾚﾚﾚﾚﾚﾚﾚﾃﾚﾚﾚﾚﾚﾚﾚﾃﾚﾚﾚﾚﾚﾚﾚwwﾃﾚｯﾃﾚｯﾃﾚｯwwʅ(´-౪-)ʃﾃﾞ─ﾝwwｹﾞｪｪﾑｵｰｳﾞｧｰwwwʅ(◜◡‾)ʃ?")
                .queue();
    }

    @Override
    public String getDescription() {
        return "SuperHexagonを発言されたチャンネルに投稿します。";
    }

    @Override
    public String getUsage() {
        return "/super";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}
