package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Alpha implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (Library.isDenyToyCmd(channel)) {
            message.reply("このチャンネルではこのコマンドを利用できません。<#616995424154157080>などで実行してください。").queue();
            return;
        }
        String msg = String.format(
                "オ、オオwwwwwwwwオレ%swwwwwwww最近めっちょ%sれてんねんオレwwwwwwww%sとかかけるとめっちょ%sやねんwwwwァァァァァァァwww%sを見下しながら食べる%sは一段とウメェなァァァァwwwwwwww",
                opt(args, 0, "アルファ"),
                opt(args, 1, "ふぁぼら"),
                opt(args, 2, "エゴサ"),
                opt(args, 3, "人気"),
                opt(args, 4, "クソアルファ"),
                opt(args, 5, "エビフィレオ"));
        channel.sendMessage(msg).queue();
    }

    @Override
    public String getDescription() {
        return "アルファになったオレを発言されたチャンネルに投稿します。「アルファ」・「ふぁぼら」・「エゴサ」・「人気」・「クソアルファ」・「エビフィレオ」を順に引数に指定することで別のテキストに置き換えができます。";
    }

    @Override
    public String getUsage() {
        return "/alpha [アルファ] [ふぁぼら] [エゴサ] [人気] [クソアルファ] [エビフィレオ]";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }

    private String opt(String[] args, int key, String defaultValue) {
        if (key < 0 || key >= args.length) {
            return defaultValue;
        }
        return args[key];
    }
}
