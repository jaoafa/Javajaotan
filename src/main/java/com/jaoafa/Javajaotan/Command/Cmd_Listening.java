package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Listening implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length == 0) {
			channel.sendMessage(member.getAsMention() + ", このコマンドを実行するには、1つ以上の引数が必要です。").queue();
			return;
		}
		jda.getPresence().setActivity(Activity.listening(String.join(" ", args)));
		channel.sendMessage(member.getAsMention() + ", NowListeningを更新しました。").queue();
	}

	@Override
	public String getDescription() {
		return "jaotanのNowListeningを更新します。";
	}

	@Override
	public String getUsage() {
		return "/listening <Text>";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}
}