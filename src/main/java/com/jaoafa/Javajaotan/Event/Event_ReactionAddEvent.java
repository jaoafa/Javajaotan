package com.jaoafa.Javajaotan.Event;

import com.jaoafa.Javajaotan.Task.Task_MeetingVote;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_ReactionAddEvent {
    @SubscribeEvent
    public void onReactionAddEvent(MessageReactionAddEvent event) {
        if (event.getChannel().getIdLong() == 597423974816808970L) {
            new Task_MeetingVote().run();
        }
    }
}
