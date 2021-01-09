package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.MuteManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Cmd_Mute implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (channel.getIdLong() != 597423467796758529L) {
            message.reply("このチャンネルではこのコマンドを使用することはできません。").queue();
            return; // #meeting以外
        }
        if (args.length == 0) {
            HashSet<String> muteList = MuteManager.refreshMuteList();
            if (muteList == null) {
                message.reply("ミュートリストをリフレッシュできませんでした。").queue();
                return;
            }
            List<String> replys = muteList.stream().map(s -> "<@" + s + ">")
                    .collect(Collectors.toList());
            message.reply(String.format("ミュート中: %s", String.join(", ", replys))).queue();
            return;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                MuteManager.addMuteList(args[1]);
                message.reply("<@" + args[1] + ">をミュートしました。").queue();
                return;
            } else if (args[0].equalsIgnoreCase("remove")) {
                MuteManager.removeMuteList(args[1]);
                message.reply("<@" + args[1] + ">のミュートを解除しました。").queue();
                return;
            }
        }
        message.reply("" + getUsage()).queue();
    }

    @Override
    public String getDescription() {
        return "ミュート関連処理をします。";
    }

    @Override
    public String getUsage() {
        return "/mute: ミュートしている一覧を表示します。\n"
                + "/mute add <UserID>: ユーザーをミュートします。\n"
                + "/mute remove <UserID>: ユーザーのミュートを解除します。";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}
