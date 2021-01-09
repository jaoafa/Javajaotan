package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class Cmd_Listening implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (args.length == 0) {
            message.reply("このコマンドを実行するには、1つ以上の引数が必要です。").queue();
            return;
        }
        jda.getPresence().setActivity(Activity.listening(String.join(" ", args)));
        message.reply("NowListeningを更新しました。").queue();
    }

    @Override
    public String getDescription() {
        return "jaotanのNowListeningを更新します。";
    }

    @Override
    public String getUsage() {
        return "/listening <Text>";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}