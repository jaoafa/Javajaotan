package com.jaoafa.Javajaotan.Task;

import com.jaoafa.Javajaotan.Lib.SubAccount;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.TimerTask;

public class Task_SubAccountCheck extends TimerTask {
    @Override
    public void run() {
        // MinecraftConnected
        JDA jda = Main.getJDA();
        Guild guild = jda.getGuildById(597378876556967936L); // new jMS Gamers Club
        if (guild == null) {
            System.out.println("guild not found.");
            return;
        }
        Role SubAccountRole = guild.getRoleById(753047225751568474L);
        if (SubAccountRole == null) {
            System.out.println("SubAccountRole not found.");
            return;
        }
        TextChannel channel = guild.getTextChannelById(769505014569500695L); // LOGGER#javajaotan_notice
        if (channel == null) {
            System.out.println("[SubAccountCheck] LOGGER#javajaotan_notice(769505014569500695) channel is not found.");
            return;
        }
        guild.getMembersWithRoles(SubAccountRole).forEach(member -> {
            SubAccount subAccount = new SubAccount(member);
            if (subAccount.isSubAccount()) {
                return;
            }
            // SubAccount権限がついているのにサブアカウントではない
            guild.removeRoleFromMember(member, SubAccountRole).queue();
            channel.sendMessage("ユーザー「" + member.getAsMention() + "」はサブアカウント登録がなされていないため、サブアカウント役職を外しました。").queue();
        });
    }
}
