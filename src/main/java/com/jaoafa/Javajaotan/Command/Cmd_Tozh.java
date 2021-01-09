package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Cmd_Tozh implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        List<String> froms = Arrays.stream(args).filter(
                arg -> arg != null && arg.startsWith("from:")).collect(Collectors.toList());
        String from = "auto";
        if (froms.size() != 0) {
            from = froms.get(0).substring("from:".length());
        }
        String to = "zh-CN";
        List<String> texts = Arrays.stream(args).filter(
                arg -> arg != null && !arg.startsWith("from:") && !arg.startsWith("to:")).collect(Collectors.toList());
        if (texts.size() == 0) {
            message.reply("引数が適切ではありません。").queue();
            return;
        }

        if (froms.size() == 0) {
            try {
                from = Library.getLang(String.join(" ", texts)); // 日本語・英語・簡体字中国語・フランス語・ドイツ語・スペイン語・タイ語
                if (from == null) {
                    from = Library.getRefineLang(String.join(" ", texts));
                }
                if (from == null) {
                    from = "auto";
                }
                if (from.equals("zh")) {
                    from = "auto";
                }
            } catch (IOException e) {
                Main.ExceptionReporter(message, e);
                from = "auto";
            }
        }

		/*String res = Library.GoogleTranslateWeb(String.join(" ", texts), from, to);
		String source = "GoogleTranslateWeb";*/
        String res = null;
        String source = null;
        if (res == null) {
            res = Library.GoogleTranslateGAS(String.join(" ", texts), from, to);
            source = "GoogleTranslateGAS";
        }
        if (res == null) {
            message.reply("翻訳に失敗しました。").queue();
            return;
        }
        message.reply("```" + String.join(" ", texts) + "```↓```" + res + "```(`"
                + from + "` -> `" + to + "` | SOURCE: `" + source + "`)").queue();
    }

    @Override
    public String getDescription() {
        return "指定されたテキストを中国語 簡体(zh-CN)に翻訳します。「from:<LANG>」を指定すると元言語を設定できます。指定しないと自動で判定します。\n"
                + "明示的にfrom:autoを指定すると、翻訳サービス側での言語判定がなされます。明示指定しないと、Javajaotan側で判定しその結果を元言語として判定します。";
    }

    @Override
    public String getUsage() {
        return "/tozh <Text...> [from:LANG]";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}