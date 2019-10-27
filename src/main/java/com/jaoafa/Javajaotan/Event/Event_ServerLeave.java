package com.jaoafa.Javajaotan.Event;

import com.jaoafa.Javajaotan.Main;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_ServerLeave {
	@SubscribeEvent
	public void onMemberLeaveEvent(GuildMemberLeaveEvent event) {
		if (event.getGuild().getIdLong() != 597378876556967936L) {
			return; // jMS Gamers Clubのみ
		}
		User user = event.getUser();
		TextChannel general = event.getGuild().getTextChannelById(597419057251090443L);
		general.sendMessage(
				":wave:" + user.getName() + "#" + user.getDiscriminator() + "がjMS Gamers Clubから退出しました。")
				.queue(
						success -> {
						},
						failure -> {
							Main.DiscordExceptionError(getClass(), general, failure);
						});
	}
}
