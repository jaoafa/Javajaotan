package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;

public class Cmd_Chat implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        Role[] allowRoles = {
                jda.getRoleById(189381504059572224L), // old jGC Admin
                jda.getRoleById(281699181410910230L), // old jGC Moderator
                jda.getRoleById(597405109290532864L), // new jGC Admin
                jda.getRoleById(597405110683041793L), // new jGC Moderator
        };
        MessageChannel sendToChannel = channel;
        if (Library.isAllowRole(member, allowRoles) || member.getIdLong() == 221991565567066112L) {
            // チャンネル指定可
            System.out.println("isAllowRole: " + Library.isAllowRole(member, allowRoles));
            if (args.length >= 2) {
                System.out.println("args.length >= 2");
                if (Library.isLong(args[0])) {
                    System.out.println("Library.isLong(args[0]): " + args[0]);
                    long channelID = Long.parseLong(args[0]);
                    if (jda.getTextChannelById(channelID) == null) {
                        channel.sendMessage(member.getAsMention() + ", 指定されたチャンネルが見つかりません。(ID指定)").queue();
                        return;
                    }
                    sendToChannel = jda.getTextChannelById(channelID);
                } else {
                    System.out.println("!Library.isLong(args[0]): " + args[0]);
                    if (jda.getTextChannelsByName(args[0], false).isEmpty()) {
                        channel.sendMessage(member.getAsMention() + ", 指定されたチャンネルが見つかりません。(チャンネル名指定)").queue();
                        return;
                    }
                    sendToChannel = jda.getTextChannelsByName(args[0], false).get(0);
                }
            }
        }
        String content;
        if (sendToChannel.getIdLong() == channel.getIdLong()) {
            // チャンネル指定なし
            content = String.join(" ", args);
        } else {
            // チャンネル指定あり
            content = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }
        sendToChannel.sendMessage(content).queue();
    }

    @Override
    public String getDescription() {
        return "指定されたチャンネルにチャットを送信します。運営未満の権限の利用者はチャンネルの指定ができません。";
    }

    @Override
    public String getUsage() {
        return "/chat [ChannelID] <Message...>";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}
