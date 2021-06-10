package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Cmd_Report implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if(args.length <= 1) {
            // 引数が0もしくは1の場合
            message.reply("このコマンドを実行するには、2つ以上の引数が必要です。").queue();
            return;
        }
        String minecraftId = args[0];
        String inputMessage = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        String suffixMessage = MessageFormat.format("reportコマンドによって{0}から報告されました。", member.getUser().getAsTag());
        String cardMessage = inputMessage + "\n\n" + suffixMessage;

        Trello trello = Main.getTrello();
        if (trello == null) {
            return;
        }

        Card card = new Card();
        card.setName(minecraftId);
        card.setDesc(cardMessage);
        trello.createCard("60816a7e6791dc5c605fb4e4", card);
    }

    @Override
    public String getDescription() {
        return "指定された内容を報告します。一部のチャンネルでのみ利用できます。";
    }

    @Override
    public String getUsage() {
        return "/report <MinecraftID> <Message...>";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}
