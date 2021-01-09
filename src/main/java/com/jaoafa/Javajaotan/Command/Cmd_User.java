package com.jaoafa.Javajaotan.Command;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.SubAccount;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Cmd_User implements CommandPremise {
    @Override
    public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
                          Message message, String[] args) {
        // player id, uuid, discord id, discord name + tag
        UserData userData = null;
        if (args.length == 0) {
            userData = fromMinecraftID(member.getId());
        } else {
            String arg = String.join(" ", args);

            // @tomachi#0310 (mention)
            if (!message.getMentionedUsers().isEmpty())
                userData = fromDiscordID(message.getMentionedUsers().get(0).getId());
            // 32ff7cdc-a1b4-450a-aa7e-6af75fe8c37c
            if (Library.isUUID(arg)) userData = fromUUID(UUID.fromString(arg));
            // 221991565567066112
            if (Library.isLong(arg) && userData == null) userData = fromDiscordID(arg);
            // 0310
            if (arg.length() == 4 && Library.isInt(arg) && userData == null)
                userData = fromDiscriminator(arg);
            // #0310 -> 0310
            if (arg.startsWith("#") && arg.substring(1).length() == 4 && Library.isInt(arg.substring(1)) && userData == null)
                userData = fromDiscriminator(arg.substring(1));
            // tomachi#0310
            if (!arg.startsWith("#") && arg.contains("#") && userData == null) userData = fromDiscordTag(arg);

            // mine_book000
            if (userData == null) userData = fromMinecraftID(arg);
            // tomachi
            if (userData == null) userData = fromDiscordName(arg);
            // tomachi
            if (userData == null) userData = fromDiscordNickName(arg);
        }

        if (userData == null) {
            message.reply("指定されたユーザー情報は見つかりませんでした。").queue();
            return;
        }

        if (!userData.isFound) {
            message.reply("指定されたユーザー情報は見つかりませんでした。(2)").queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .addField("MinecraftID", userData.minecraftID, false)
                .addField("Discord", userData.discordName + "#" + userData.discordDiscriminator, false)
                .addField("PermissionGroup", userData.minecraftPermissionGroup, false)
                .addField("Minecraft-Discord Connect", Boolean.toString(userData.discordConnected), false)
                .addField("DiscordNickName", userData.discordNickname, false)
                .addField("Minecraft LastLogin", userData.minecraftLastLogin, false)
                .addField("MinecraftUUID", userData.minecraftUUID, false)
                .addField("DiscordID", userData.discordID, false)
                .setColor(Color.GREEN)
                .setTimestamp(Instant.now());

        if (!userData.discordID.equals("null"))
            builder.addField("DiscordUser", "https://discordapp.com/users/" + userData.discordID, false);
        if (!userData.minecraftUUID.equals("null"))
            builder.addField("UserPage", "https://jaoafa.com/user/" + userData.minecraftUUID, false);
        if (!userData.minecraftUUID.equals("null"))
            builder.setThumbnail("https://crafatar.com/renders/body/" + userData.minecraftUUID.replaceAll("-", ""));

        if (!userData.discordID.equals("null")) {
            SubAccount sub = new SubAccount(Long.parseLong(userData.discordID));
            builder.addField("DiscordMainAccount", sub.getMainAccount() != null ? sub.getMainAccount().getUser().getAsMention() : "null", false);
            builder.addField(
                    "DiscordSubAccount",
                    sub.getSubAccounts().isEmpty() ?
                            "null" :
                            sub.getSubAccounts().stream().map(account -> account.getUser().getAsMention()).collect(Collectors.joining(", ")),
                    false);
        }

        message.reply(builder.build()).append(member.getAsMention()).queue();
    }

    private UserData fromUUID(UUID uuid) {
        System.out.println("Cmd_User.fromUUID(): " + uuid.toString());
        try {
            String url = "https://api.jaoafa.com/users/" + uuid.toString();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                return null;
            }
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            JSONObject json = new JSONObject(body.string());
            response.close();

            if (!json.has("status")) {
                return null;
            }
            if (!json.getBoolean("status")) {
                return null;
            }
            if (!json.has("data")) {
                return null;
            }

            JSONObject data = json.getJSONObject("data");

            UserData userData = new UserData(data.getString("mcid"), data.getString("uuid"), data.getString("permission"), data.getString("lastlogin"));
            if (!data.has("discordid")) {
                return userData;
            }
            String discordId = data.getString("discordid");
            if (userData.addDiscordUserData(discordId)) userData.discordConnected = true;

            return userData;
        } catch (IOException e) {
            return null;
        }
    }

    private UserData fromDiscordID(String discordId) {
        System.out.println("Cmd_User.fromDiscordID(): " + discordId);
        UserData userData = new UserData();
        if (!userData.addDiscordUserData(discordId)) {
            return null;
        }
        try {
            String url = "https://api.jaoafa.com/users/" + discordId;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                return userData;
            }
            ResponseBody body = response.body();
            if (body == null) {
                return userData;
            }
            JSONObject json = new JSONObject(body.string());
            response.close();

            if (!json.has("status")) {
                return userData;
            }
            if (!json.getBoolean("status")) {
                return userData;
            }
            if (!json.has("data")) {
                return userData;
            }

            JSONObject data = json.getJSONObject("data");

            userData.minecraftID = data.getString("mcid");
            userData.minecraftUUID = data.getString("uuid");
            userData.minecraftPermissionGroup = data.getString("permission");
            userData.minecraftLastLogin = data.getString("lastlogin");
            userData.discordConnected = true;

            return userData;
        } catch (IOException e) {
            return userData;
        }
    }

    private UserData fromDiscriminator(String discriminator) {
        System.out.println("Cmd_User.fromDiscriminator(): " + discriminator);
        JDA jda = Main.getJDA();
        Guild guild = jda.getGuildById(597378876556967936L);
        if (guild == null) return null;

        List<Member> members = guild.getMembers().stream().filter(member -> member.getUser().getDiscriminator().equals(discriminator)).collect(Collectors.toList());
        if (members.size() != 1) return null;

        Member member = members.get(0);
        return fromDiscordID(member.getId());
    }

    private UserData fromDiscordTag(String tag) {
        System.out.println("Cmd_User.fromDiscordTag(): " + tag);
        JDA jda = Main.getJDA();
        Guild guild = jda.getGuildById(597378876556967936L);
        if (guild == null) return null;

        List<Member> members = guild.getMembers().stream().filter(member -> member.getUser().getAsTag().equals(tag)).collect(Collectors.toList());
        if (members.size() != 1) return null;

        Member member = members.get(0);
        return fromDiscordID(member.getId());
    }

    private UserData fromMinecraftID(String mcid) {
        System.out.println("Cmd_User.fromMinecraftID(): " + mcid);
        try {
            String url = "https://api.jaoafa.com/users/" + mcid;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                return null;
            }
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            JSONObject json = new JSONObject(body.string());
            response.close();

            if (!json.has("status")) {
                return null;
            }
            if (!json.getBoolean("status")) {
                return null;
            }
            if (!json.has("data")) {
                return null;
            }

            JSONObject data = json.getJSONObject("data");

            UserData userData = new UserData(data.getString("mcid"), data.getString("uuid"), data.getString("permission"), data.getString("lastlogin"));
            if (!data.has("discordid")) {
                return userData;
            }
            String discordId = data.getString("discordid");
            if (userData.addDiscordUserData(discordId)) userData.discordConnected = true;

            return userData;
        } catch (IOException e) {
            return null;
        }
    }

    private UserData fromDiscordName(String discordName) {
        System.out.println("Cmd_User.fromDiscordName(): " + discordName);
        JDA jda = Main.getJDA();
        Guild guild = jda.getGuildById(597378876556967936L);
        if (guild == null) return null;

        List<Member> members = guild.getMembersByName(discordName, false);
        if (members.size() != 1) return null;

        Member member = members.get(0);
        return fromDiscordID(member.getId());
    }

    private UserData fromDiscordNickName(String discordNickName) {
        System.out.println("Cmd_User.fromDiscordNickName(): " + discordNickName);
        JDA jda = Main.getJDA();
        Guild guild = jda.getGuildById(597378876556967936L);
        if (guild == null) return null;

        List<Member> members = guild.getMembersByNickname(discordNickName, false);
        if (members.size() != 1) return null;

        Member member = members.get(0);
        return fromDiscordID(member.getId());
    }


    @Override
    public String getDescription() {
        return "指定されたDiscordユーザー・Minecraftプレイヤーの情報を表示します。";
    }

    @Override
    public String getUsage() {
        return "/user <DiscordID/MinecraftID/MinecraftUUID/DiscordName/DiscordDiscriminator/DiscordNickName>";
    }

    @Override
    public boolean isjMSOnly() {
        return true;
    }

    static class UserData {
        public boolean isFound = false;
        public String minecraftID = "null";
        public String minecraftUUID = "null";
        public String minecraftPermissionGroup = "null";
        public String minecraftLastLogin = "null";
        public boolean discordConnected = false;
        public String discordID = "null";
        public String discordName = "null";
        public String discordDiscriminator = "null";
        public String discordNickname = "null";

        UserData() {
        }

        UserData(String minecraftID, String minecraftUUID, String minecraftPermissionGroup, String minecraftLastLogin) {
            this.isFound = true;
            this.minecraftID = minecraftID;
            this.minecraftUUID = minecraftUUID;
            this.minecraftPermissionGroup = minecraftPermissionGroup;
            this.minecraftLastLogin = minecraftLastLogin;
        }

        public boolean addDiscordUserData(String discordID) {
            JDA jda = Main.getJDA();
            try {
                User user = jda.retrieveUserById(discordID).complete();
                if (user == null) return false;
                this.discordID = discordID;
                this.discordName = user.getName();
                this.discordDiscriminator = user.getDiscriminator();
                this.isFound = true;
            } catch (ErrorResponseException e) {
                return false;
            }

            try {
                Guild guild = jda.getGuildById(597378876556967936L);
                if (guild == null) return true;
                Member member = guild.retrieveMemberById(discordID).complete();
                if (member == null) return true;
                this.discordNickname = member.getNickname() != null ? member.getNickname() : "null";
                return true;
            } catch (ErrorResponseException e) {
                return true;
            }
        }
    }
}
