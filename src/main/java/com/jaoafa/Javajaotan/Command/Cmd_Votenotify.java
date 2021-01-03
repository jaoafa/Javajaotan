package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Lib.VoteNotify;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Cmd_Votenotify implements CommandPremise {
    static Set<String> mcjp_strings = new HashSet<>();
    static Set<String> mono_strings = new HashSet<>();

    static {
        mcjp_strings.add("mcjp");
        mcjp_strings.add("minecraft.jp");

        mono_strings.add("mono");
        mono_strings.add("monocraft");
        mono_strings.add("monocraft.net");
    }

    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            channel.sendMessage(member.getAsMention() + ", データベースサーバに接続できません。時間をおいて再度お試しください。(`MySQLDBManager null`)")
                    .queue();
            return;
        }
        try {
            if (args.length == 0) {
                // 現在の設定表示
                onCommand_GetSettings(jda, guild, channel, member, message, args);
            } else if (args.length == 2) {
                // /votenotify mcjp <off>
                if (!args[1].equalsIgnoreCase("off")) {
                    channel.sendMessage(String.format("%s, `off`以外を指定する場合は時間を指定してください。\n例: `/votenotify mcjp everyday 00`", member.getAsMention())).queue();
                    return;
                }
                onCommand_Disable(jda, guild, channel, member, message, args);
            } else if (args.length == 3) {
                // /votenotify mcjp <everyday, before> <Hour>
                onCommand_Enable(jda, guild, channel, member, message, args);
            }
        } catch (SQLException e) {
            Main.ExceptionReporter(channel, e);
        }
    }

    void onCommand_GetSettings(JDA jda, Guild guild, MessageChannel channel, Member member,
                               Message message, String[] args) throws SQLException {
        VoteNotify votenotify = new VoteNotify(member.getIdLong());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("VoteNotify");
        embed.setDescription(member.getAsMention());
        if (votenotify.getMCJPTime() == -1) {
            embed.addField("minecraft.jp : 現在の設定", "オフ", false);
        } else {
            if (votenotify.getMCJPType().equals("everyday")) {
                embed.addField("minecraft.jp : 現在の設定", "オン (毎日通知)", false);
            } else if (votenotify.getMCJPType().equals("before")) {
                embed.addField("minecraft.jp : 現在の設定", "オン (前日に投票した場合のみ", false);
            } else {
                embed.addField("minecraft.jp : 現在の設定", String.format("オン (%s)", votenotify.getMCJPType()), false);
            }
            embed.addField("minecraft.jp : 通知時刻", String.format("%s時", String.format("%02d", votenotify.getMCJPTime())), false);
        }
        if (votenotify.getMONOTime() == -1) {
            embed.addField("monocraft.net : 現在の設定", "オフ", false);
        } else {
            if (votenotify.getMONOType().equals("everyday")) {
                embed.addField("monocraft.net : 現在の設定", "オン (毎日通知)", false);
            } else if (votenotify.getMONOType().equals("before")) {
                embed.addField("monocraft.net : 現在の設定", "オン (前日に投票した場合のみ", false);
            } else {
                embed.addField("monocraft.net : 現在の設定", String.format("オン (%s)", votenotify.getMONOType()), false);
            }
            embed.addField("monocraft.net : 通知時刻", String.format("%s時", String.format("%02d", votenotify.getMONOTime())), false);
        }

        channel.sendMessage(embed.build()).queue();
    }

    void onCommand_Enable(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) throws SQLException {
        VoteNotify votenotify = new VoteNotify(member.getIdLong());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("VoteNotify");
        embed.setDescription(member.getAsMention());

        if (!args[1].equalsIgnoreCase("everyday") && !args[1].equalsIgnoreCase("before")) {
            channel.sendMessage(
                    member.getAsMention() + ", 第2引数には`everyday`, `before`, `off`のみを指定できます。`off`を指定する場合は第3引数は不要です。\n"
                            + "例: `/votenotify mcjp everyday 00`\n"
                            + "例: `/votenotify mcjp off`")
                    .queue();
            return;
        }
        if (!Library.isInt(args[2])) {
            channel.sendMessage(member.getAsMention() + ", 第3引数には数値を指定してください。").queue();
            return;
        }
        int hour = Integer.parseInt(args[2]);
        if (hour < 0 || hour > 23) {
            channel.sendMessage(member.getAsMention() + ", 第3引数には00～23を指定してください。").queue();
            return;
        }
        String type = args[1].equalsIgnoreCase("everyday") ? "everyday" : "before";
        String type_ja = args[1].equalsIgnoreCase("everyday") ? "毎日通知" : "前日に投票した場合のみ";
        if (mcjp_strings.contains(args[0])) {
            // mcjp -> everyday or before
            votenotify.enable("mcjp", hour, type);
            channel.sendMessage(member.getAsMention() + ", minecraft.jpの投票お知らせを「" + type_ja + "」・「"
                    + String.format("%02d", hour) + "時」に設定しました。\n" +
                    "jaotanからのDM受信設定(フレンドへの追加・サーバメンバーからのDM許可等)が許可されていないとDMが送信されませんので、今一度ご確認ください。")
                    .queue();
        } else if (mono_strings.contains(args[0])) {
            // mono -> everyday or before
            votenotify.enable("mono", hour, type);
            channel.sendMessage(member.getAsMention() + ", monocraft.netの投票お知らせを「" + type_ja + "」・「"
                    + String.format("%02d", hour) + "時」に設定しました。\n" +
                    "jaotanからのDM受信設定(フレンドへの追加・サーバメンバーからのDM許可等)が許可されていないとDMが送信されませんので、今一度ご確認ください。")
                    .queue();
        } else {
            channel.sendMessage(member.getAsMention() + ", 第1引数には`" + String.join(", ", mcjp_strings) + "`, `"
                    + String.join(", ", mono_strings) + "`のいずれかを指定してください。\n"
                    + "例: `/votenotify mcjp everyday 00`").queue();
        }
    }

    void onCommand_Disable(JDA jda, Guild guild, MessageChannel channel, Member member,
                           Message message, String[] args) throws SQLException {
        VoteNotify votenotify = new VoteNotify(member.getIdLong());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("VoteNotify");
        embed.setDescription(member.getAsMention());
        if (mcjp_strings.contains(args[0])) {
            // mcjp -> off
            votenotify.disable("mcjp");
            channel.sendMessage(member.getAsMention() + ", minecraft.jpの投票お知らせを無効化しました。").queue();
        } else if (mono_strings.contains(args[0])) {
            // mono -> off
            votenotify.disable("mono");
            channel.sendMessage(member.getAsMention() + ", monocraft.netの投票お知らせを無効化しました。").queue();
        } else {
            channel.sendMessage(member.getAsMention() + ", 第1引数には`" + String.join(", ", mcjp_strings) + "`, `"
                    + String.join(", ", mono_strings) + "`のいずれかを指定してください。\n"
                    + "例: `/votenotify mcjp everyday 00`").queue();
        }
    }

    @Override
    public String getDescription() {
        return "未投票時に通知する機能の設定を行えます。";
    }

    @Override
    public String getUsage() {
        return "/votenotify <mcjp, mono> <everyday, before, off> <Hour>";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}
