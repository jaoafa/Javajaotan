package com.jaoafa.Javajaotan.Command;

import java.util.List;
import java.util.stream.Collectors;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.MuteManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Mute implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (channel.getIdLong() != 597423467796758529L) {
			channel.sendMessage(member.getAsMention() + ", このチャンネルではこのコマンドを使用することはできません。").queue();
			return; // #meeting以外
		}
		if (args.length == 0) {
			List<String> replys = MuteManager.refreshMuteList().stream().map(s -> "<@" + s + ">")
					.collect(Collectors.toList());
			channel.sendMessage("ミュート中: " + String.join(", ", replys)).queue();
			return;
		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("add")) {
				MuteManager.addMuteList(args[1]);
				channel.sendMessage(member.getAsMention() + ", <@" + args[1] + ">をミュートしました。").queue();
				return;
			} else if (args[0].equalsIgnoreCase("remove")) {
				MuteManager.removeMuteList(args[1]);
				channel.sendMessage(member.getAsMention() + ", <@" + args[1] + ">のミュートを解除しました。").queue();
				return;
			}
		}
		channel.sendMessage(member.getAsMention() + ", " + getUsage()).queue();
	}

	@Override
	public String getDescription() {
		return "ミュート関連処理をします。";
	}

	@Override
	public String getUsage() {
		return "/mute: ミュートしている一覧を表示します。\n"
				+ "/mute add <UserID>: ユーザーをミュートします。\n"
				+ "/mute remove <UserID>: ユーザーのミュートを解除します。";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}
}
