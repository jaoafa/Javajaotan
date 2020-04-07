package com.jaoafa.Javajaotan.Channel;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jaoafa.Javajaotan.ChannelPremise;
import com.jaoafa.Javajaotan.Lib.Library;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;

public class Channel_597423370589700098 implements ChannelPremise {
	// #support
	@Override
	public void run(JDA jda, Guild guild, MessageChannel channel, Member member, Message message,
			boolean edited) {
		if (message.getType() != MessageType.DEFAULT) {
			return;
		}
		String text = message.getContentRaw();
		Pattern p = Pattern.compile("#([0-9]+) \\D*?(\\-?[0-9]+) \\D*?(\\-?[0-9]+)");
		Matcher m = p.matcher(text);

		if (m.groupCount() == 0) {
			return;
		}

		String debug = "";
		LinkedList<Integer> X = new LinkedList<>();
		LinkedList<Integer> Z = new LinkedList<>();
		while (m.find()) {
			String keynum = m.group(1);
			String X_str = m.group(2);
			String Z_str = m.group(3);

			X.add(Integer.valueOf(X_str));
			Z.add(Integer.valueOf(Z_str));

			debug += "Added #" + keynum + " : " + X_str + " " + Z_str + "\n";
		}

		if (X.size() == 0 || Z.size() == 0) {
			return;
		}

		double blocks = Library.calcBlockNumber(X, Z);

		String system_msg = "特に問題はありません。";
		if (!Library.checkBlocks(X, Z)) {
			system_msg = "範囲指定が不適切です。時計回りまたは反時計回りに指定してください。";
		} else if (blocks != Math.floor(blocks)) {
			system_msg = "ブロック数が不適切(整数でない)です。範囲が正確に指定されていない可能性があります。";
		} else if (blocks >= 2500000) {
			system_msg = "拡張最大制限ブロック数(2,500,000ブロック)以上です。「自治体関連方針」により原則的に認可できません。";
		} else if (blocks >= 250000) {
			system_msg = "初期規定ブロック数(250,000ブロック)以上です。新規登録の場合は「規定ブロック数を超える明確な理由」が必要です。";
		} else if (Math.round(blocks) == 0) {
			system_msg = "範囲情報を入力してください。";
		}

		channel.sendMessage(member.getAsMention() + ", " + blocks + " Blocks (" + X.size() + ")\n"
				+ "メッセージ: `" + system_msg + "`\n"
				+ "\n"
				+ "範囲指定に誤りがあり、修正を行う場合は申請のメッセージを削除するなど、__**明確に申請の取り消し**__を行ってください。\n"
				+ "また、申請後に未だ認可がされてない状態で申請内容を変更したい場合、__**申請のメッセージを編集するのではなく再度申請をし直して**__ください。"
				+ "```" + debug + "```").queue();
	}

	@Override
	public boolean isAlsoTargetEdited() {
		return false;
	}
}
