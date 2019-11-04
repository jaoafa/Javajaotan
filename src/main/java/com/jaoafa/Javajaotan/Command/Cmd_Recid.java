package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Recid implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		channel.sendMessage("はい、調子乗って俺のこと叩いてくるやろ お前ほんまに覚えとけよ ガチで仕返ししたるからな ほんまにキレタ 絶対許さん お前のID控えたからな\n"
				+ "\n"
				+ "`" + member.getUser().getId() + "` \\_φ(･\\_･").queue();
	}

	@Override
	public String getDescription() {
		return "IDをメモするメッセージを発言されたチャンネルに投稿します。";
	}

	@Override
	public String getUsage() {
		return "/recid";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}