package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cmd_Dice implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        List<String> list = new ArrayList<>();
        list.add(":one:");
        list.add(":two:");
        list.add(":three:");
        list.add(":four:");
        list.add(":five:");
        list.add(":six:");

        Random r = new Random();
        int old = 0;
        String firstSelect = list.get(0);
        Message post_message = channel.sendMessage(firstSelect + " `[-1]`").complete();
        if (post_message == null) {
            channel.sendMessage(member.getAsMention() + ", コマンドを実行できませんでした。").queue();
            return;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        for (int i = 0; i < r.nextInt(5) + 5; i++) {
            int selectNum = r.nextInt(list.size());
            while (old + selectNum == list.size()) {
                selectNum = r.nextInt(list.size());
            }
            String select = list.get(selectNum);

            post_message.editMessage(select + " `[" + i + "]`").complete();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        post_message.editMessage(":arrows_counterclockwise:").complete();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        int selectNum = r.nextInt(list.size());
        while (old + selectNum == list.size()) {
            selectNum = r.nextInt(list.size());
        }
        String select = list.get(selectNum);
        post_message.editMessage(select + " `[END]`").complete();
    }

    @Override
    public String getDescription() {
        return "さいころを振り、その答えを返します。";
    }

    @Override
    public String getUsage() {
        return "/dice";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }
}
