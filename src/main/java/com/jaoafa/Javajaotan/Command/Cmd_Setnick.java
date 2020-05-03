package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Setnick implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length == 0) {
			channel.sendMessage(member.getAsMention() + ", このコマンドを実行するには、1つ以上の引数が必要です。").queue();
			return;
		}
		Member jaotan = guild.getMember(jda.getSelfUser());
		String oldnick = jaotan.getNickname();
		if (oldnick == null)
			oldnick = "null";
		jaotan.modifyNickname(String.join(" ", args)).queue(null, failure -> {
			Main.DiscordExceptionError(getClass(), channel, failure);
		});
		channel.sendMessage(
				member.getAsMention() + ", jaotan nickname | `" + oldnick + "` -> `" + String.join(" ", args) + "`")
				.queue();
	}

	@Override
	public String getDescription() {
		return "指定した文字列をjaotanのニックネームに設定します。";
	}

	@Override
	public String getUsage() {
		return "/setnick <Text>";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}
