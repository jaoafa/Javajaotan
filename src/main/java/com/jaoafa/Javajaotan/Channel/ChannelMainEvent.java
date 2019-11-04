package com.jaoafa.Javajaotan.Channel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.jaoafa.Javajaotan.ChannelPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.MuteManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class ChannelMainEvent {
	@SubscribeEvent
	public void onMessageReceivedEvent(MessageReceivedEvent event) {
		JDA jda = event.getJDA();
		if (!event.isFromType(ChannelType.TEXT)) {
			return;
		}
		Guild guild = event.getGuild();
		MessageChannel channel = event.getChannel();
		Member member = event.getMember();
		Message message = event.getMessage();

		if (message.isWebhookMessage()) {
			return;
		}
		if (MuteManager.isMuted(member.getUser().getId())) {
			return;
		}
		if (event.getAuthor().getIdLong() == 222018383556771840L) {
			return;
		}

		try {
			String className = channel.getId();
			//channel.sendMessage("com.jaoafa.Javajaotan.Command.Cmd_" + className);

			Class.forName("com.jaoafa.Javajaotan.Channel.Channel_" + className);
			// クラスがない場合これ以降進まない
			Constructor<?> construct = Class
					.forName("com.jaoafa.Javajaotan.Channel.Channel_" + className).getConstructor();
			ChannelPremise cmd = (ChannelPremise) construct.newInstance();

			cmd.run(jda, guild, channel, member, message, false);
		} catch (ClassNotFoundException e) {
			// not found
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// error
			Main.ExceptionReporter(channel, e);
		}
	}

	@SubscribeEvent
	public void onMessageUpdateEvent(MessageUpdateEvent event) {
		JDA jda = event.getJDA();
		Guild guild = event.getGuild();
		MessageChannel channel = event.getChannel();
		Member member = event.getMember();
		Message message = event.getMessage();

		if (MuteManager.isMuted(member.getUser().getId())) {
			return; // Muted
		}
		if (event.getAuthor().getIdLong() == 222018383556771840L) {
			return;
		}

		try {
			String className = channel.getId();
			//channel.sendMessage("com.jaoafa.Javajaotan.Command.Cmd_" + className);

			Class.forName("com.jaoafa.Javajaotan.Channel.Channel_" + className);
			// クラスがない場合これ以降進まない
			Constructor<?> construct = Class
					.forName("com.jaoafa.Javajaotan.Channel.Channel_" + className).getConstructor();
			ChannelPremise chan = (ChannelPremise) construct.newInstance();

			if (!chan.isAlsoTargetEdited()) {
				return;
			}

			chan.run(jda, guild, channel, member, message, true);
		} catch (ClassNotFoundException e) {
			// not found
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// error
			Main.ExceptionReporter(channel, e);
		}
	}
}
