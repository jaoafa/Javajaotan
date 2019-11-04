package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Powa implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		channel.sendMessage("ポわ～～～～～～～ｗｗｗｗ！！！ｗ！ｗｗ！ｗ！ｗ").queue();
	}

	@Override
	public String getDescription() {
		return "「ポわ～～～～～～～ｗｗｗｗ！！！ｗ！ｗｗ！ｗ！ｗ」を発言されたチャンネルに投稿します。";
	}

	@Override
	public String getUsage() {
		return "/powa";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}