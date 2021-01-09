package com.jaoafa.Javajaotan.ALLChat;

import com.jaoafa.Javajaotan.ALLChatPremise;
import com.jaoafa.Javajaotan.Lib.ClassFinder;
import com.jaoafa.Javajaotan.Lib.MuteManager;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.lang.reflect.Constructor;

public class ALLChatMainEvent {
    @SubscribeEvent
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        JDA jda = event.getJDA();
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        Guild guild = event.getGuild();
        MessageChannel channel = event.getChannel();
        Member member = event.getMember();
        User user = event.getAuthor();
        Message message = event.getMessage();

        if (channel.getIdLong() == 603841992404893707L) {
            return; // #greeting
        }
        if (message.isWebhookMessage()) {
            return;
        }
        if (MuteManager.isMuted(member.getUser().getId())) {
            return; // Muted
        }
        if (event.getAuthor().getIdLong() == 222018383556771840L) {
            return;
        }

        try {
            ClassFinder classFinder = new ClassFinder();
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.Javajaotan.ALLChat")) {
                if (!clazz.getName().startsWith("com.jaoafa.Javajaotan.ALLChat.ALL_")) {
                    continue;
                }
                Constructor<?> construct = clazz.getConstructor();
                ALLChatPremise allchat = (ALLChatPremise) construct.newInstance();
                allchat.run(jda, guild, channel, member, user, message, false);
            }
        } catch (Exception e) {
            Main.ExceptionReporter(message, e);
        }
    }

    @SubscribeEvent
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        JDA jda = event.getJDA();
        Guild guild = event.getGuild();
        MessageChannel channel = event.getChannel();
        Member member = event.getMember();
        User user = event.getAuthor();
        Message message = event.getMessage();

        if (channel.getIdLong() == 603841992404893707L) {
            return; // #greeting
        }

        if (MuteManager.isMuted(member.getUser().getId())) {
            return; // Muted
        }
        if (event.getAuthor().getIdLong() == 222018383556771840L) {
            return;
        }

        try {
            ClassFinder classFinder = new ClassFinder();
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.Javajaotan.ALLChat")) {
                if (!clazz.getName().startsWith("com.jaoafa.Javajaotan.ALLChat.ALL_")) {
                    continue;
                }
                if (clazz.getEnclosingClass() != null) {
                    continue;
                }
                if (clazz.getName().contains("$")) {
                    continue;
                }
                Constructor<?> construct = clazz.getConstructor();
                ALLChatPremise allchat = (ALLChatPremise) construct.newInstance();

                if (!allchat.isAlsoTargetEdited()) {
                    continue;
                }

                allchat.run(jda, guild, channel, member, user, message, true);
            }
        } catch (Exception e) {
            Main.ExceptionReporter(message, e);
        }
    }
}
