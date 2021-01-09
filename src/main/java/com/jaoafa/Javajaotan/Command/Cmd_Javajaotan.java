package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Task.Task_AccountConnectChecker;
import com.jaoafa.Javajaotan.Task.Task_MeetingVote;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Javajaotan implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("task_meetingvote")) {
            new Task_MeetingVote(true).run();
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("task_accountconnectchecker")) {
            new Task_AccountConnectChecker().run();
        }
    }

    @Override
    public String getDescription() {
        return "Javajaotan debug Command";
    }

    @Override
    public String getUsage() {
        return "/javajaotan ...";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}
