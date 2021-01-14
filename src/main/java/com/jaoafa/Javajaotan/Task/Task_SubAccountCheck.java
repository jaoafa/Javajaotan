package com.jaoafa.Javajaotan.Task;

import com.jaoafa.Javajaotan.Lib.SubAccount;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashSet;
import java.util.Set;
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
        Role MinecraftConnectedRole = guild.getRoleById(604011598952136853L);
        if (MinecraftConnectedRole == null) {
            System.out.println("MinecraftConnectedRole not found.");
            return;
        }
        Role SubAccountRole = guild.getRoleById(753047225751568474L);
        if (SubAccountRole == null) {
            System.out.println("SubAccountRole not found.");
            return;
        }
        TextChannel channel = guild.getTextChannelById(799275935445549068L); // LOGGER#javajaotan-notice
        if (channel == null) {
            System.out.println("[SubAccountCheck] LOGGER#javajaotan-notice(799275935445549068) channel is not found.");
            return;
        }
        Set<Member> MinecraftConnecteds = new HashSet<>(guild.getMembersWithRoles(MinecraftConnectedRole));

        guild.getMembersWithRoles(SubAccountRole).forEach(member -> {
            SubAccount subAccount = new SubAccount(member);
            if (subAccount.isSubAccount()) {
                boolean isMainConnected = MinecraftConnecteds.stream().anyMatch(memb -> memb.getIdLong() == subAccount.getMainAccount().getDiscordId());
                if (isMainConnected) {
                    return;
                }
            }
            // SubAccount権限がついているのにサブアカウントではない
            guild.removeRoleFromMember(member, SubAccountRole).queue();
            channel.sendMessage("ユーザー「" + member.getAsMention() + "」はサブアカウント登録がなされていないか、メインアカウントが存在しないため、サブアカウント役職を外しました。").queue();
        });
    }
}
