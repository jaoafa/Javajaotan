package com.jaoafa.Javajaotan.Event;

import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_ServerJoin {
    long lastjoinuserid = -1;

    @SubscribeEvent
    public void onMemberJoinEvent(GuildMemberJoinEvent event) {
        if (event.getGuild().getIdLong() != 597378876556967936L) {
            return; // jMS Gamers Clubのみ
        }
        User user = event.getUser();
        long userid = user.getIdLong();

        if (lastjoinuserid == userid) {
            return;
        }
        lastjoinuserid = userid;

        TextChannel general = event.getGuild().getTextChannelById(597419057251090443L);
        TextChannel greeting = event.getGuild().getTextChannelById(603841992404893707L);
        if (general == null) {
            System.out.println("general is null.");
            return;
        }
        general.sendMessage(String.format(":man_dancing:<@%s>(#%s)さんがjMS Gamers Clubに参加しました！", user.getId(), user.getDiscriminator()))
                .queue(
                        null,
                        failure -> Main.DiscordExceptionError(getClass(), general, failure)
                );
        if (greeting == null) {
            System.out.println("general is null.");
            return;
        }
        greeting.sendMessage(
                String.format(
                        ":man_dancing:<@%s>(#%s)さん、jao Minecraft Server Discordにようこそ。\n" +
                                "__**運営方針により、参加から10分以内に発言がない場合システムによって自動的にキック**__されます。<#603841992404893707>チャンネルで「jao」「afa」とあいさつしてみましょう！",
                        user.getId(), user.getDiscriminator()))
                .queue(
                        null,
                        failure -> Main.DiscordExceptionError(getClass(), general, failure)
                );
    }
}
