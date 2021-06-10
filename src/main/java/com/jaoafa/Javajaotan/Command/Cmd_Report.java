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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Cmd_Report implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        if (args.length <= 1) {
            // 引数が0もしくは1の場合
            message.reply("このコマンドを実行するには、2つ以上の引数が必要です。").queue();
            return;
        }

        if (channel.getIdLong() != 597423444501463040L) {
            message.reply("このコマンドはこのチャンネルでは利用できません。").queue();
            return;
        }

        String minecraftId = args[0];
        UUID uuid = getUUID(minecraftId);
        String placeBreakCounter = null;
        if (uuid != null) {
            PlaceBreak pb = getPlaceBreakCount(uuid);
            if (pb != null) {
                placeBreakCounter = String.format("Place: %d | Break: %d", pb.placeCount, pb.breakCount);
            }
        }

        String inputMessage = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        String suffixMessage = MessageFormat.format("reportコマンドによって{0}から報告されました。", member.getUser().getAsTag());
        LinkedList<String> cardMessages = new LinkedList<>();
        cardMessages.add(inputMessage);
        cardMessages.add("");
        if (placeBreakCounter != null) {
            cardMessages.add("---");
            cardMessages.add("");
            cardMessages.add("- " + placeBreakCounter);
        }
        cardMessages.add("- " + suffixMessage);

        String cardMessage = String.join("\n", cardMessages);

        Trello trello = Main.getTrello();
        if (trello == null) {
            message.reply("Trelloがアクティブでないため、この動作を実施できませんでした。").queue();
            return;
        }

        Card card = new Card();
        card.setName(minecraftId);
        card.setDesc(cardMessage);
        List<Message.Attachment> attachments = message.getAttachments();
        String listId = "60af2b797a0f72620c62c28b";
        if (!attachments.isEmpty()) {
            listId = "60816a7e6791dc5c605fb4e5";
        }

        card = trello.createCard(listId, card);

        for (Message.Attachment attachment : attachments) {
            trello.addUrlAttachmentToCard(card.getId(), attachment.getUrl());
        }

        message.reply("作成しました: " + card.getUrl()).queue();
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

    UUID getUUID(String minecraftId) {
        try {
            OkHttpClient client = new OkHttpClient();
            String url = String.format("https://api.jaoafa.com/v1/users/%s", minecraftId);
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String result = Objects.requireNonNull(response.body()).string();
            JSONObject obj = new JSONObject(result);
            if (obj.has("status") && obj.getBoolean("status")) {
                // statusがあって、trueのとき
                return UUID.fromString(obj.getJSONObject("data").getString("uuid"));
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    PlaceBreak getPlaceBreakCount(UUID uuid) {
        try {
            OkHttpClient client = new OkHttpClient();
            String url = String.format("https://api.jaoafa.com/v1/world/coreprotect/%s?info=true", uuid.toString());
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String result = Objects.requireNonNull(response.body()).string();
            JSONObject obj = new JSONObject(result);
            if (obj.has("status") && obj.getBoolean("status")) {
                // statusがあって、trueのとき
                int placeCount = obj.getJSONObject("data").getInt("place");
                int breakCount = obj.getJSONObject("data").getInt("break");

                return new PlaceBreak(placeCount, breakCount);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class PlaceBreak {
        int placeCount;
        int breakCount;

        public PlaceBreak(int placeCount, int breakCount) {
            this.placeCount = placeCount;
            this.breakCount = breakCount;
        }
    }
}
