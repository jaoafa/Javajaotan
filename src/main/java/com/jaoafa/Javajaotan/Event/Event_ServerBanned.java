package com.jaoafa.Javajaotan.Event;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_ServerBanned {
    @SubscribeEvent
    public void onMemberBannedEvent(GuildBanEvent event) {
        if (event.getGuild().getIdLong() != 597378876556967936L) {
            return; // jMS Gamers Clubのみ
        }
        User user = event.getUser();
        TextChannel general = event.getGuild().getTextChannelById(597419057251090443L);
        if (general == null) {
            System.out.println("general channel not found.");
            return;
        }
        general.sendMessage(String.format(":no_pedestrians:%s#%sがjMS Gamers ClubからBanされました。", user.getName(), user.getDiscriminator())).queue();
    }
}
