package com.jaoafa.Javajaotan.Channel;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jaoafa.Javajaotan.ChannelPremise;
import com.jaoafa.Javajaotan.Lib.Library;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;

public class Channel_597423974816808970 implements ChannelPremise {
	// #meeting_vote
	@Override
	public void run(JDA jda, Guild guild, MessageChannel channel, Member member, Message message,
			boolean edited) {
		if (message.getType() != MessageType.DEFAULT) {
			return;
		}
		String content = message.getContentDisplay();
		String border = null;
		if (content.contains("\n")) {
			String[] contents = content.split("\n");
			Pattern p = Pattern.compile("\\[Border:([0-9]+)\\]");
			Matcher m = p.matcher(contents[0]);
			if (m.find()) {
				border = m.group(1);
			}
		}
		//String pinerr = null;
		message.pin().complete();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, 2);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("NEW VOTE");
		builder.appendDescription(
				"@here " + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + "から新しい投票です。");
		builder.addField("賛成の場合", "投票メッセージに対して:thumbsup:を付けてください。", false);
		builder.addField("反対の場合", "投票メッセージに対して:thumbsdown:を付けてください。\n"
				+ "**反対の場合は<#597423467796758529>に意見理由を必ず書いてください。**", false);
		builder.addField("白票の場合", "投票メッセージに対して:flag_white:を付けてください。\n"
				+ "(白票の場合投票権利を放棄し他の人に投票を委ねます)", false);
		builder.addField("この投票に対する話し合い", "<#597423467796758529>でどうぞ。", false);
		if (border != null && Library.isInt(border)) {
			builder.addField("決議ボーダー", "この投票の決議ボーダーは" + border + "です。", false);
		}
		builder.addField("その他", "投票の有効会議期限は2週間(" + sdf.format(cal.getTime()) + "まで)です。", false);
		/*if (pinerr != null) {
			builder.addField("ピン留めエラーメッセージ", pinerr, false);
		}*/
		builder.setColor(Color.YELLOW);
		channel.sendMessage(builder.build()).queue();
	}

	@Override
	public boolean isAlsoTargetEdited() {
		return false;
	}
}
