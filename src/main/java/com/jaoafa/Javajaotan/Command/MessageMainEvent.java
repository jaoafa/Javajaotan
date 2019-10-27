package com.jaoafa.Javajaotan.Command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.MuteManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class MessageMainEvent {
	@SubscribeEvent
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("Ready: " + event.getJDA().getSelfUser().getName() + "#"
				+ event.getJDA().getSelfUser().getDiscriminator());

		Main.setJDA(event.getJDA());

		Main.ReportChannel = event.getJDA().getTextChannelById(597766057117351937L);

		TextChannel channel = event.getJDA().getTextChannelById(597766057117351937L);
		if (channel != null) {
			channel.sendMessage("**[" + Library.sdfFormat(new Date()) + " | " + Library.getHostName() + "]** "
					+ "Start Javajaotan");
		}
		Runtime.getRuntime().addShutdownHook(new Thread(
				() -> {
					TextChannel _channel = event.getJDA().getTextChannelById(597766057117351937L);
					if (_channel != null) {
						_channel.sendMessage(
								"**[" + Library.sdfFormat(new Date()) + " | " + Library.getHostName() + "]** "
										+ "End Javajaotan");
					}
				}));
	}

	@SubscribeEvent
	public void onMessageReceivedEvent(MessageReceivedEvent event) {
		JDA jda = event.getJDA();
		Guild guild = event.getGuild();
		MessageChannel channel = event.getChannel();
		Member member = event.getMember();
		Message message = event.getMessage();
		String text = event.getMessage().getContentRaw();

		if (MuteManager.isMuted(member.getUser().getId())) {
			return;
		}
		if (event.getAuthor().getIdLong() == 222018383556771840L) {
			return;
		}
		if (channel.getIdLong() == 603841992404893707L) {
			return;
		}

		if (!text.startsWith("/")) {
			return;
		}
		System.out.println("Msg: " + event.getAuthor().getName() + " " + event.getMessage().getContentRaw());

		String[] args;
		String cmdname;
		if (text.contains(" ")) {
			cmdname = text.split(" ")[0].substring(1).trim();
			args = Arrays.copyOfRange(text.split(" "), 1, text.split(" ").length);
			args = Arrays.stream(args)
					.filter(s -> (s != null && s.length() > 0))
					.toArray(String[]::new);
		} else {
			args = new String[] {};
			cmdname = text.substring(1).trim();
		}
		try {
			String className = cmdname.substring(0, 1).toUpperCase() + cmdname.substring(1).toLowerCase(); // Help

			Class.forName("com.jaoafa.Javajaotan.Command.Cmd_" + className);
			// クラスがない場合これ以降進まない
			Constructor<?> construct = Class.forName("com.jaoafa.Javajaotan.Command.Cmd_" + className)
					.getConstructor();
			CommandPremise cmd = (CommandPremise) construct.newInstance();

			if (cmd.isjMSOnly() && guild.getIdLong() != 597378876556967936L) {
				return;
			}

			cmd.onCommand(jda, guild, channel, member, message, args);
		} catch (ClassNotFoundException e) {
			// not found
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// error
			Main.ExceptionReporter(channel, e);
		}
	}
}
