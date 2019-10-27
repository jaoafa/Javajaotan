package com.jaoafa.Javajaotan.Event;

import com.jaoafa.Javajaotan.Main;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_ServerJoin {
	@SubscribeEvent
	public void onMemberJoinEvent(GuildMemberJoinEvent event) {
		if (event.getGuild().getIdLong() != 597378876556967936L) {
			return; // jMS Gamers Clubのみ
		}
		User user = event.getUser();

		TextChannel general = event.getGuild().getTextChannelById(597419057251090443L);
		TextChannel greeting = event.getGuild().getTextChannelById(603841992404893707L);

		general.sendMessage(
				":man_dancing:<@" + user.getId() + ">(#" + user.getDiscriminator() + ")さんがjMS Gamers Clubに参加しました。")
				.queue(
						success -> {
						},
						failure -> {
							Main.DiscordExceptionError(getClass(), general, failure);
						});
		greeting.sendMessage(
				":man_dancing:<@" + user.getId() + ">(#" + user.getDiscriminator()
						+ ")さん、jao Minecraft Server Discordにようこそ。\n"
						+ "運営方針により、参加から10分以内に発言がない場合システムによって自動的にキックされます。<#603841992404893707>チャンネルで「jao」「afa」とあいさつしてみましょう。")
				.queue(
						success -> {
						},
						failure -> {
							Main.DiscordExceptionError(getClass(), general, failure);
						});
	}
}
