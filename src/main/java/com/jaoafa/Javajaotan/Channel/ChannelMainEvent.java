package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import com.jaoafa.Javajaotan.Lib.MuteManager;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
        User user = event.getAuthor();
        Message message = event.getMessage();

        if (message.isWebhookMessage()) {
            if (channel.getIdLong() != 626727474922913792L && channel.getIdLong() != 597423974816808970L) {
                // #develop_todo、#meeting_voteだけ例外
                return;
            }
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

            if (Main.getImplementeds().contains("Channel_" + className)) {
                System.out.println("Channel_" + className + " is implemented in Javajaotan2 (Channel)");
                return;
            }

            cmd.run(jda, guild, channel, member, user, message, false);
        } catch (ClassNotFoundException e) {
            // not found
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // error
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

            chan.run(jda, guild, channel, member, user, message, true);
        } catch (ClassNotFoundException e) {
            // not found
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // error
            Main.ExceptionReporter(message, e);
        }
    }
}
