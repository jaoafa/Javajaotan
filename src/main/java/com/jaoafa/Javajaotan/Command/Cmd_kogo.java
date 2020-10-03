package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.json.JSONObject;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class Cmd_kogo implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (args.length == 0) {
            channel.sendMessage(member.getAsMention() + ", " + getUsage()).queue();
            return;
        }
        String original = String.join(" ", args);
        String json = getRunCommand(message, "node", "/var/jaoafa/Javajaotan/extcmds/kogo/run.js", original);
        JSONObject obj = new JSONObject(json);
        channel.sendMessage(member.getAsMention() + ", ```" + obj.optString("output", "null") + "```").queue();
    }

    private String getRunCommand(Message message, String... command) {
        MessageChannel channel = message.getChannel();
        Process p;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            builder.directory(new File("/var/jaoafa/Javajaotan/"));
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
        return "指定されたテキストを古文にします。 http://catincat.jp/javascript/kogo3.html";
    }

    @Override
    public String getUsage() {
        return "/kogo <Text>";
    }

    @Override
    public boolean isjMSOnly() {
        return false;
    }
}