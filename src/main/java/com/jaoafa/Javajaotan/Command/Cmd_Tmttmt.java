package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Tmttmt implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		channel.sendMessage("とまとぉwとまとぉw ( https://youtu.be/v372aagNItc )").queue();
	}

	@Override
	public String getDescription() {
		return "「とまとぉwとまとぉw ( https://youtu.be/v372aagNItc )」をリプライします。";
	}

	@Override
	public String getUsage() {
		return "/tmttmt";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}