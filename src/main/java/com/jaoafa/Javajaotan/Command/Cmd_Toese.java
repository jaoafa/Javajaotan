package com.jaoafa.Javajaotan.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.jaoafa.Javajaotan.CommandPremise;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * @see https://github.com/orangelinux/CorrectJP-NEW/
 */
public class Cmd_Toese implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		Map<String, String> map = new HashMap<String, String>() {
			{
				put("多", "乡");
				put("鳥", "乌");
				put("雨", "丽");
				put("両", "两");
				put("並", "业");
				put("メルカリ", "淘宝網");
				put("AQUOS", "HUAWEI");
				put("aquos", "HUAWEI");
				put("Aquos", "HUAWEI");
				put("huaweimk", "ファーウェイウェイ");
				put("あなた", "貴様");
				put("貴", "贵");
				put("し", "レ");
				put("ぶ", "ふ");
				put("で", "て");
				put("応", "应");
				put("ス", "ヌ");
				put("雑", "杂");
				put("貨", "货");
				put("見", "见");
				put("潰", "溃");
				put("め", "ぬ");
				put("キ", "ギ");
				put("ぞ", "そ");
				put("舐", "舐");
				put("絶", "绝");
				put("対", "对");
				put("得", "慧");
				put("溜", "贮");
				put("達", "们");
				put("jp", "cn");
				put("NHK", "CCTV");
				put("XPERIA", "HUAWEI");
				put("円", "人民元");
				put("LINEpay", "alipay");
				put("PayPay", "WechatPay");
				put("Twitter", "weibo");
				put("ツイッター", "ウェイボ");
				put("instagram", "Tiktok");
				put("インスタ", "Tiktok");
				put("ライン", "wechat");
				put("LINE", "wechat");
				put("line", "wechat");
				put("風", "风");
				put("なさい", "(しなさい)");
				put("強", "强");
				put("東京", "北京");
				put("シリコンバレー", "深圳");
				put("google", "百度");
				put("グーグル", "百度");
				put("Google", "百度");
				put("スカイツリー", "上海中心");
				put("SKY TREE", "shanghai tower");
				put("TOKYO", "上海");
				put("ハ", "八゜");
				put("amazon", "亚马逊");
				put("アマゾン", "亚马逊");
				put("乘", "乘");
				put("黑", "黑");
				put("snapdragon", "Kirin");
				put("SD", "NM");
				put("PUBG", "荒野行動");
				put("労働", "極度勞動");
				put("東", "东");
				put("ラーメン", "うーメソ");
				put("🇯🇵", "🇨🇳");
				put("🇰🇷", "🇨🇳");
				put("🇺🇸", "🇨🇳");
				put("🇬🇧", "🇨🇳");
				put("🇷🇺", "🇨🇳");
				put("🇩🇪", "🇨🇳");
				put("🇮🇳", "🇨🇳");
				put("🇿🇦", "🇨🇳");
				put("🇧🇷", "🇨🇳");
				put("オ", "才");
				put("愛", "爱");
				put("語", "语");
				put("ぬ", "ゐ");
				put("る", "ゑ");
				put("iphone", "HUAWEI");
				put("アイフォン", "ファーウェイ");
				put("だ", "た");
				put("変", "變");
				put("榮", "荣");
				put("強", "强");
				put("う", "ラ");
				put("ハ", "八");
				put("応", "应");
				put("偉", "伟");
				put("義", "义");
				put("結", "结");
				put("協", "协");
				put("調", "调");
				put("剤", "剂");
				put("様", "樣");
				put("セ", "乜");
				put("動", "动");
				put("評", "评");
				put("ファーウェイ", "华为技术有限公司");
				put("HUAWEI", "华为技术有限公司");
			}
		};
		String content = String.join(" ", args);
		for (Entry<String, String> entry : map.entrySet()) {
			content = content.replaceAll(Pattern.quote(entry.getKey()), entry.getValue());
		}
		channel.sendMessage(member.getAsMention() + ", ```" + content + "```").queue();
	}

	@Override
	public String getDescription() {
		return "怪レい日本语を生成レます。";
	}

	@Override
	public String getUsage() {
		return "/toese <Message...>";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}