package com.jaoafa.Javajaotan.ALLChat;

import com.jaoafa.Javajaotan.ALLChatPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

public class ALL_Calledjaotan implements ALLChatPremise {
    @Override
    public void run(JDA jda, Guild guild, MessageChannel channel, Member member, User user, Message message,
                    boolean edited) {
        String text = message.getContentRaw();
        if ((channel.getName().contains("server") && channel.getName().contains("chat"))
                || channel.getName().contains("console")) {
            // チャンネル名に「server」と「chat」が含まれるチャンネル
            // 「console」が含まれるチャンネル
            // は無効化
            return;
        }
        if (text.equals("jaotan")) {
            message.reply("はいっ！お呼びですか？").queue();
        }
        if (text.equals("Jaotan")) {
            message.reply("はいっ！お呼びで…はい？\njaotanは``jaotan``であって``Jaotan``じゃないです！人の名前を間違えるなんてひどい！人間のCrime！")
                    .queue();
        }
        if (!text.equals("jaotan") && !text.equals("Jaotan") && text.equalsIgnoreCase("jaotan")) {
            // 「jaotan」でもなく「Jaotan」でもないjaotan。つまりjAotanとかjaoTanとか。
            message.reply("はいっ！お呼びで…。ああ、論外です。御帰り願います。").queue();
        }
		/*
		if (!text.equalsIgnoreCase("jaotan") && text.contains("jaotan")) {
			RequestBuffer.request(() -> {
				try {
					message.reply("はいっ！あっ、呼んだわけではないんですね…");
				} catch (DiscordException discordexception) {
					Javajaotan.DiscordExceptionError(getClass(), channel, discordexception);
				}
			});
		}
		*/
    }

    @Override
    public boolean isAlsoTargetEdited() {
        return false;
    }
}