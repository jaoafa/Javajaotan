package com.jaoafa.Javajaotan.Command;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.Library;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Cmd_Blookup implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (channel.getIdLong() != 597423444501463040L) {
			channel.sendMessage(member.getAsMention() + ", このチャンネルでは使用できません。").queue();
			return;
		}
		if (args.length == 1) {
			// /blookup <Player>
			UUID uuid = getUUID(channel, member, args[0]);
			if (uuid == null) {
				return;
			}
			int userid = getUserID(channel, member, uuid);
			if (userid == -1) {
				return;
			}
			processBlockLookup(jda, guild, channel, member, message, args[0], userid, -1, -1);
		} else if (args.length == 2) {
			// /blookup <cp|lb> <Player>
			// /blookup <Player> <before　rowid>
			if (args[0].equalsIgnoreCase("cp")) {
				// /blookup <cp> <Player>
				UUID uuid = getUUID(channel, member, args[1]);
				if (uuid == null) {
					return;
				}
				int userid = getUserID(channel, member, uuid);
				if (userid == -1) {
					return;
				}
				processBlockLookup(jda, guild, channel, member, message, args[1], userid, -1, -1);
				return;
			} else if (args[0].equalsIgnoreCase("lb")) {
				// /blookup <lb> <Player>
				channel.sendMessage(member.getAsMention() + ", Unimplemented").queue();
				return;
			} else {
				// /blookup <Player> <before　rowid>
				UUID uuid = getUUID(channel, member, args[0]);
				if (uuid == null) {
					return;
				}
				int userid = getUserID(channel, member, uuid);
				if (userid == -1) {
					return;
				}
				if (!Library.isInt(args[1])) {
					channel.sendMessage(member.getAsMention() + ", `<before rowid>`には数値を指定してください。");
					return;
				}
				processBlockLookup(jda, guild, channel, member, message, args[0], userid, Integer.parseInt(args[1]),
						-1);
				return;
			}
		} else if (args.length == 3) {
			// /blookup <cp|lb> <Player> <before　rowid>
			// /blookup <X> <Y> <Z> - Jao_Afaワールド
		} else if (args.length == 4) {
			// /blookup <cp|lb> <Player> <before/after> <rowid>
			// /blookup [cp|lb] <X> <Y> <Z>
			// /blookup [world] <X> <Y> <Z>
		} else if (args.length == 5) {
			// /blookup [cp|lb] [world] <X> <Y> <Z>
		}
	}

	void processBlockLookup(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String inputPlayerName, int userid, int before, int after) {
		LinkedList<String> retMessages = new LinkedList<>();

		retMessages.add("__**Blookup - `" + inputPlayerName + "` (" + userid + ")**__");
		retMessages.add("");

		JSONObject response = getLookup(channel, member, userid, before, after);
		for (int i = 0; i < response.getJSONArray("data").length(); i++) {
			JSONObject d = response.getJSONArray("data").getJSONObject(i);

			JSONObject location = d.getJSONObject("location");
			String world = location.getJSONObject("world").getString("name");
			int x = location.getInt("x");
			int y = location.getInt("y");
			int z = location.getInt("z");

			JSONObject block = d.getJSONObject("block");
			String blockName = block.getString("material").replace("minecraft:", "") + ":" + block.getInt("data");

			String action = d.getBoolean("action") ? "placed" : "destroyed";

			boolean rollbacked = d.getBoolean("rolled_back");

			Date date = new Date(d.getLong("time") * 1000);
			String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);

			retMessages.add((rollbacked ? "~~" : "") +
					String.format("%9s `%20s` in `%7s %5d %5d %5d` at `%s`", action, blockName, world, x, y, z, time)
					+ (rollbacked ? "~~" : ""));
		}

		retMessages.add("");
		retMessages.add(String.format("old: `/blookup %s %d` | new: `/blookup %s %d`",
				inputPlayerName, response.getInt("prevId"),
				inputPlayerName, response.getInt("nextId")));

		channel.sendMessage(member.getAsMention() + ", " + String.join("\n", retMessages)).queue();
	}

	UUID getUUID(MessageChannel channel, Member member, String str) {
		if (isUUID(str)) {
			return UUID.fromString(str);
		}
		String url = "https://api.jaoafa.com/users/" + str;

		try {
			OkHttpClient client = new OkHttpClient().newBuilder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS)
					.build();
			Request request = new Request.Builder().url(url).build();

			Response response = client.newCall(request).execute();
			if (response.code() != 200 && response.code() != 302) {
				channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: " + response.code() + " "
						+ response.body().string()).queue();
				response.close();
				return null;
			}

			JSONObject json = new JSONObject(response.body().string());
			response.close();
			if (!json.getBoolean("status")) {
				channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: UUIDの取得に失敗しました。\n```"
						+ json.optString("message", "null") + "```").queue();
				return null;
			}

			String uuidStr = json.getJSONObject("data").getString("uuid");
			return UUID.fromString(uuidStr);
		} catch (IOException ex) {
			channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: " + ex.getMessage()).queue();
			return null;
		}
	}

	int getUserID(MessageChannel channel, Member member, UUID uuid) {
		String url = "https://api.jaoafa.com/world/coreprotect/" + uuid.toString();

		try {
			OkHttpClient client = new OkHttpClient().newBuilder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS)
					.build();
			Request request = new Request.Builder().url(url).build();

			Response response = client.newCall(request).execute();
			if (response.code() != 200 && response.code() != 302) {
				channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: " + response.code() + " "
						+ response.body().string()).queue();
				response.close();
				return -1;
			}

			JSONObject json = new JSONObject(response.body().string());
			response.close();
			if (!json.getBoolean("status")) {
				channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: ユーザーIDの取得に失敗しました。\n```"
						+ json.optString("message", "null") + "```").queue();
				return -1;
			}

			return json.getJSONObject("data").getInt("userid");
		} catch (IOException ex) {
			channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: " + ex.getMessage()).queue();
			return -1;
		}
	}

	JSONObject getLookup(MessageChannel channel, Member member, int userid, int before, int after) {
		String url = "https://api.jaoafa.com/world/coreprotect/lookup/" + userid + "?";

		if (before != -1) {
			url += "before=" + before;
		} else if (after != -1) {
			url += "after=" + after;
		}

		try {
			OkHttpClient client = new OkHttpClient().newBuilder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS)
					.build();
			Request request = new Request.Builder().url(url).build();

			Response response = client.newCall(request).execute();
			if (response.code() != 200 && response.code() != 302) {
				channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: " + response.code() + " "
						+ response.body().string()).queue();
				response.close();
				return null;
			}

			JSONObject json = new JSONObject(response.body().string());
			response.close();
			if (!json.getBoolean("status")) {
				channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: ブロックデータの取得に失敗しました。\n```"
						+ json.optString("message", "null") + "```").queue();
				return null;
			}

			return json;
		} catch (IOException ex) {
			channel.sendMessage(member.getAsMention() + ", APIサーバへの接続に失敗: " + ex.getMessage()).queue();
			return null;
		}
	}

	boolean isUUID(String str) {
		try {
			UUID.fromString(str);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public String getDescription() {
		return "特定のプレイヤーが行ったブロックの編集情報を表示します。";
	}

	@Override
	public String getUsage() {
		return "/blookup <PlayerName|UUID>";
	}

	@Override
	public boolean isjMSOnly() {
		return true;
	}
}
