package com.jaoafa.Javajaotan.Event;

import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateNameEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_TomachiEmojis {
	@SubscribeEvent
	public void onEmoteAddedEvent(EmoteAddedEvent event) {
		if (event.getGuild().getIdLong() != 627851806990663724L) {
			return; // Tomachi Emojisのみ
		}
		Emote emote = event.getEmote();
		ListedEmote listemote = event.getGuild().retrieveEmoteById(emote.getIdLong()).complete();
		User user = listemote.getUser();

		TextChannel emojiLog = event.getGuild().getTextChannelById(645864591775105034L);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(":new:NEW EMOJI : " + emote.getAsMention() + " (`:" + emote.getName() + ":`)");
		builder.setThumbnail(emote.getImageUrl());
		builder.setAuthor(user.getName() + "#" + user.getAsTag(), "https://discordapp.com/users/" + user.getId(),
				user.getAvatarUrl());
		builder.setTimestamp(Instant.now());
		emojiLog.sendMessage(builder.build()).queue();
	}

	@SubscribeEvent
	public void onEmoteUpdateNameEvent(EmoteUpdateNameEvent event) {
		if (event.getGuild().getIdLong() != 627851806990663724L) {
			return; // Tomachi Emojisのみ
		}
		Emote emote = event.getEmote();
		ListedEmote listemote = event.getGuild().retrieveEmoteById(emote.getIdLong()).complete();
		User user = listemote.getUser();

		TextChannel emojiLog = event.getGuild().getTextChannelById(645864591775105034L);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(":repeat:CHANGE EMOJI : " + emote.getAsMention()
				+ " (`:" + event.getOldName() + ":` -> `:" + event.getNewName() + ":`)");
		builder.setThumbnail(emote.getImageUrl());
		builder.setAuthor(user.getName() + "#" + user.getAsTag(), "https://discordapp.com/users/" + user.getId(),
				user.getAvatarUrl());
		builder.setTimestamp(Instant.now());
		emojiLog.sendMessage(builder.build()).queue();
	}

	@SubscribeEvent
	public void onEmoteRemovedEvent(EmoteRemovedEvent event) {
		if (event.getGuild().getIdLong() != 627851806990663724L) {
			return; // Tomachi Emojisのみ
		}
		Emote emote = event.getEmote();

		TextChannel emojiLog = event.getGuild().getTextChannelById(645864591775105034L);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(":wave:DELETED EMOJI : (`:" + emote.getName() + ":`)");
		builder.setThumbnail(emote.getImageUrl());
		builder.setTimestamp(Instant.now());
		emojiLog.sendMessage(builder.build()).queue();
	}
}
