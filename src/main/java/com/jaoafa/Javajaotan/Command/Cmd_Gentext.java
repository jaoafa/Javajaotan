package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.json.JSONObject;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class Cmd_Gentext implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (Library.isDenyToyCmd(channel)) {
            channel.sendMessage(member.getAsMention() + ", このチャンネルではこのコマンドを利用できません。<#616995424154157080>などで実行してください。").queue();
            return;
        }
        String json;
        if (args.length == 1 && args[0].startsWith("news")) {
            json = getRunCommand(message, "php", "/home/server/ZakuroHat/Javajaotan/extcmds/gentext.php", "news");
        } else if (args.length == 1 && args[0].startsWith("rasyoumon")) {
            json = getRunCommand(message, "php", "/home/server/ZakuroHat/Javajaotan/extcmds/gentext.php", "rasyoumon");
        } else {
            json = getRunCommand(message, "php", "/home/server/ZakuroHat/Javajaotan/extcmds/gentext.php");
        }
        JSONObject obj = new JSONObject(json);
        channel.sendMessage(member.getAsMention() + ", ```" + obj.optString("output", "null") + "```").queue();
    }

    private String getRunCommand(Message message, String... command) {
        MessageChannel channel = message.getChannel();
        Process p;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            builder.directory(new File("/home/server/ZakuroHat/Javajaotan/"));
            builder.redirectErrorStream(true);
            p = builder.start();
            p.waitFor(10, TimeUnit.MINUTES);
        } catch (IOException e) {
            Main.ExceptionReporter(channel, e);
            return "null IOException";
        } catch (InterruptedException e) {
            Main.ExceptionReporter(channel, e);
            return "null InterruptedException";
        }
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder text = new StringBuilder();
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                text.append(line).append("\n");
            }
            br.close();
            is.close();
        } catch (IOException e) {
            Main.ExceptionReporter(channel, e);
            return "null IOException";
        }
        return text.toString();
    }

    @Override
    public String getDescription() {
        return "文章を自動生成します。";
    }

    @Override
    public String getUsage() {
        return "/gentext [rasyoumon|news]";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}
