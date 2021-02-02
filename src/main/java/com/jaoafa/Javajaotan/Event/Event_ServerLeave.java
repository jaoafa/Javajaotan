package com.jaoafa.Javajaotan.Event;

import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_ServerLeave {
    @SubscribeEvent
    public void onMemberLeaveEvent(GuildMemberRemoveEvent event) {
        if (event.getGuild().getIdLong() != 597378876556967936L) {
            return; // jMS Gamers Clubのみ
        }
        User user = event.getUser();
        TextChannel general = event.getGuild().getTextChannelById(597419057251090443L);
        if (general == null) {
            System.out.println("general channel not found.");
            return;
        }
        general.sendMessage(
                String.format(":wave:%s#%s(<@%s>)がjMS Gamers Clubから退出しました。", user.getName(), user.getDiscriminator(), user.getId()))
                .queue(
                        null,
                        failure -> Main.DiscordExceptionError(getClass(), general, failure)
                );
    }
}
