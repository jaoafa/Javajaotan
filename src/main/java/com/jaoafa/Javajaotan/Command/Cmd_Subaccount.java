package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Lib.SubAccount;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cmd_Subaccount implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (channel.getIdLong() != 597423467796758529L) {
            message.reply("このチャンネルではこのコマンドを使用できません。").queue();
            return;
        }
        // /subaccount add <SubAccount> <MainAccount>
        // /subaccount remove <SubAccount>
        // /subaccount list
        // userコマンドにも相互表示。

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                onCommand_List(jda, guild, channel, member, message, args);
                return;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                onCommand_Remove(jda, guild, channel, member, message, args);
                return;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add")) {
                onCommand_Add(jda, guild, channel, member, message, args);
                return;
            }
        }
        message.reply("" + getUsage()).queue();
    }

    private void onCommand_Add(JDA jda, Guild guild, MessageChannel channel, Member member, Message message, String[] args) {
        if (!Library.isLong(args[1])) {
            message.reply("SubAccountにはサブアカウントのDiscordUserIDを指定してください。").queue();
            return;
        }
        if (!Library.isLong(args[2])) {
            message.reply("MainAccountにはメインアカウントのDiscordUserIDを指定してください。").queue();
            return;
        }
        long subid = Long.parseLong(args[1]);
        long mainid = Long.parseLong(args[2]);
        SubAccount sub = new SubAccount(subid);
        SubAccount main = new SubAccount(mainid);

        if (!main.isExists()) {
            message.reply("指定されたメインアカウントが存在しません。").queue();
            return;
        }
        if (main.isSubAccount()) {
            message.reply("指定されたメインアカウントにはサブアカウントが存在します。").queue();
            return;
        }
        if (!sub.isExists()) {
            message.reply("指定されたサブアカウントが存在しません。").queue();
            return;
        }
        if (sub.getMainAccount() != null) {
            message.reply("このサブアカウントには既にメインアカウント「" + sub.getMainAccount().getUser().getAsTag() + "」が設定されています。").queue();
            return;
        }

        Guild jMSGuild = Main.getJDA().getGuildById(597378876556967936L);
        if (jMSGuild != null) {
            Member _member;
            try {
                _member = jMSGuild.retrieveMember(main.getUser()).complete();
            } catch (ErrorResponseException e) {
                message.reply("指定されたメインアカウントはjMS Gamers Clubに参加していない可能性があります: " + e.getMeaning()).queue();
                return;
            }
            if (_member.getRoles().stream().noneMatch(role -> role.getIdLong() == 604011598952136853L)) {
                message.reply("指定されたメインアカウントはMinecraftアカウントとの接続が行われていません。`/link`によるアカウントの連携を指示してください。").queue();
                return;
            }
        } else {
            message.reply("jMS Gamers Clubのデータ取得に失敗しました。").queue();
            return;
        }

        boolean bool = sub.setMainAccount(main);
        message.reply("サブアカウントの設定に" + (bool ? "成功" : "失敗") + "しました。").queue();
    }

    private void onCommand_Remove(JDA jda, Guild guild, MessageChannel channel, Member member, Message message, String[] args) {
        if (!Library.isLong(args[1])) {
            message.reply("MainAccountにはサブアカウントのDiscordUserIDを指定してください。").queue();
            return;
        }
        long subid = Long.parseLong(args[1]);
        SubAccount sub = new SubAccount(subid);

        boolean bool = sub.removeMainAccount();
        message.reply("指定されたサブアカウントとメインアカウントの接続を解除" + (bool ? "しました。" : "できませんでした。")).queue();
    }

    private void onCommand_List(JDA jda, Guild guild, MessageChannel channel, Member member, Message message, String[] args) {
        try {
            MySQLDBManager manager = Main.MySQLDBManager;
            Connection conn = manager.getConnection();

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM subaccount");
            ResultSet res = stmt.executeQuery();
            List<String> list = new ArrayList<>();
            while (res.next()) {
                list.add(String.format("%s#%s -> %s#%s", res.getString("name"), res.getString("discriminator"), res.getString("main_name"), res.getString("main_discriminator")));
            }
            message.reply("SubAccount list / data size: " + list.size() + "```" + String.join("\n", list) + "```").queue();
        } catch (SQLException e) {
            message.reply("データの取得に失敗しました。").queue();
        }
    }

    @Override
    public String getDescription() {
        return "サブアカウントに関する処理を実施します。(特定のチャンネルでのみ使用可能)";
    }

    @Override
    public String getUsage() {
        return "/subaccount add <SubAccount> <MainAccount>\n" +
                "/subaccount remove <SubAccount>\n" +
                "/subaccount list";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}