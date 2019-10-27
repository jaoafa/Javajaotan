package com.jaoafa.Javajaotan.Event;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_TodoCheck {
	@SubscribeEvent
	public void onReactionAddEvent(MessageReactionAddEvent event) {
		MessageChannel channel = event.getChannel();
		if (channel.getIdLong() != 597424023621599232L && channel.getIdLong() != 626727474922913792L) {
			return;
		}

		event.getTextChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
			if (!message.isPinned()) {
				return;
			}
			message.retrieveReactionUsers("\u2705").queue(white_check_mark -> {
				if (white_check_mark.size() == 0) {
					return;
				}
				message.removeReaction("🆕").queue();
				message.addReaction("\u2705").queue();
				if (message.isPinned()) {
					message.unpin().queue();
				}
			});
		});
	}

	@SubscribeEvent
	public void onMessageSendEvent(MessageReceivedEvent event) {
		MessageChannel channel = event.getChannel();
		Message message = event.getMessage();
		if (message.getType() == MessageType.DEFAULT) {
			return;
		}
		// #todo | #develop_todo
		if (channel.getIdLong() != 597424023621599232L && channel.getIdLong() != 626727474922913792L) {
			return;
		}
		// jaotan
		if (event.getAuthor().getIdLong() != 222018383556771840L) {
			return;
		}
		message.delete().queue();
	}
}
