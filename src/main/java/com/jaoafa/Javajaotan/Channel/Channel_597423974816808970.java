package com.jaoafa.Javajaotan.Channel;

import com.jaoafa.Javajaotan.ChannelPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Channel_597423974816808970 implements ChannelPremise {
    // #meeting_vote
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        if (message.getType() != MessageType.DEFAULT) {
            return;
        }
        String content = message.getContentDisplay();
        String border = null;
        if (content.contains("\n")) {
            String[] contents = content.split("\n");
            Pattern p = Pattern.compile("\\[Border:([0-9]+)]");
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
                "@here " + user.getName() + "#" + user.getDiscriminator() + "から新しい投票です。");
        builder.addField("賛成の場合", "投票メッセージに対して:thumbsup:を付けてください。", false);
        builder.addField("反対の場合", "投票メッセージに対して:thumbsdown:を付けてください。\n"
                + "**反対の場合は<#597423467796758529>に意見理由を必ず書いてください。**", false);
        builder.addField("白票の場合", "投票メッセージに対して:flag_white:を付けてください。\n"
                + "(白票の場合投票権利を放棄し他の人に投票を委ねます)", false);
        builder.addField("この投票に対する話し合い", "<#597423467796758529>でどうぞ。", false);
        if (border != null && Library.isInt(border)) {
            builder.addField("決議ボーダー", "この投票の決議ボーダーは" + border + "です。", false);
        }

        String blocksdata = getBlocks(content);
        if (blocksdata != null) {
            builder.addField("Blocks Counter", blocksdata, false);
        }

        builder.addField("その他", "投票の有効会議期限は2週間(" + sdf.format(cal.getTime()) + "まで)です。", false);
		/*if (pinerr != null) {
			builder.addField("ピン留めエラーメッセージ", pinerr, false);
		}*/
        builder.setColor(Color.YELLOW);
        channel.sendMessage(builder.build()).queue();
    }

    String getBlocks(String content) {
        Pattern p = Pattern.compile("#([0-9]+) \\D*?(-?[0-9]+) \\D*?(-?[0-9]+)");
        Matcher m = p.matcher(content);

        if (m.groupCount() == 0) {
            return null;
        }

        LinkedList<Integer> X = new LinkedList<>();
        LinkedList<Integer> Z = new LinkedList<>();
        while (m.find()) {
            String X_str = m.group(2);
            String Z_str = m.group(3);

            X.add(Integer.valueOf(X_str));
            Z.add(Integer.valueOf(Z_str));
        }

        if (X.size() == 0 || Z.size() == 0) {
            return null;
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

        return blocks + " Blocks (" + X.size() + ")\n" + system_msg;
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}
