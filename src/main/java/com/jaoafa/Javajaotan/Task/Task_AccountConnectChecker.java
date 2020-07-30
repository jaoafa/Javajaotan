package com.jaoafa.Javajaotan.Task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Task_AccountConnectChecker extends TimerTask {
	Guild guild = null;
	Role MinecraftConnected = null;
	TextChannel channel = null;
	Role Verified = null;
	Role Regular = null;

	@Override
	public void run() {
		JDA jda = Main.getJDA();
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return;
		}
		guild = jda.getGuildById(597378876556967936L); // Guild: jMS Gamers Club
		if (guild == null) {
			System.out.println("[Task_AccountConnectChecker] getGuildById failed");
			return;
		}
		MinecraftConnected = guild.getRoleById(604011598952136853L); // Role: MinecraftConnected
		if (MinecraftConnected == null) {
			System.out.println("[Task_AccountConnectChecker] getRoleById[MinecraftConnected] failed");
			return;
		}
		Verified = guild.getRoleById(597405176969560064L);
		if (Verified == null) {
			System.out.println("[Task_AccountConnectChecker] getRoleById[Verified] failed");
			return;
		}
		Regular = guild.getRoleById(597405176189419554L);
		if (Regular == null) {
			System.out.println("[Task_AccountConnectChecker] getRoleById[Regular] failed");
			return;
		}
		channel = guild.getTextChannelById(716029409525497936L); // Channel: #accountconnected
		if (channel == null) {
			System.out.println("[Task_AccountConnectChecker] getTextChannelById[accountconnected] failed");
			return;
		}

		// Guildに所属するメンバーをチェック
		guild.loadMembers()
				.onSuccess(members -> {
					members.stream().forEach(member -> processMember(member)); // まず処理
					processLinkedAndLeaved(members); // 参加していないメンバーを処理
				})
				.onError(err -> {
					System.out.println("[Task_AccountConnectChecker] Error: " + err.getClass().getName());
					err.printStackTrace();
				});
	}

	/**
	 * Guildに所属するメンバーを一人一人処理
	 * @param member
	 */
	private void processMember(Member member) {
		if (member.getUser().isBot()) {
			// Bot -> 除外
			return;
		}
		try {
			MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM discordlink WHERE disid = ? AND disabled = ?");
			statement.setString(1, member.getId());
			statement.setBoolean(2, false);
			ResultSet res = statement.executeQuery();
			boolean isConnected = true;
			if (!res.next()) {
				// not found
				isConnected = false;
			}

			boolean isMinecraftConnected = member.getRoles().stream()
					.filter(role -> role != null && role.getIdLong() == MinecraftConnected.getIdLong())
					.count() != 0;
			if (isMinecraftConnected && !isConnected) {
				// MinecraftConnectedロールが付与されていて、連携されていない。
				// -> MinecraftConnectedロールを外す。
				guild.removeRoleFromMember(member, MinecraftConnected).queue(v -> {
					notify("`" + member.getUser().getAsTag()
							+ "`: MinecraftConnectedロールが設定されていましたが、連携されていないためロールを外しました。");
				}, failure -> {
					notify("`" + member.getUser().getAsTag()
							+ "`: MinecraftConnectedロールが設定されていましたが、連携されていないためロールを外そうとしましたが失敗しました。\n"
							+ "`" + failure.getClass().getName() + "` -> `" + failure.getMessage() + "`");
				});
				removePermissionRoles(member);
				return;
			} else if (!isMinecraftConnected && isConnected) {
				// MinecraftConnectedロールが付与されていなくて、連携されている。
				// -> MinecraftConnectedロールを付ける。続行
				guild.addRoleToMember(member, MinecraftConnected).queue(v -> {
					notify("`" + member.getUser().getAsTag() + "`: MinecraftConnectedロールを設定しました。");
				}, failure -> {
					notify("`" + member.getUser().getAsTag() + "`: MinecraftConnectedロールを設定しようとしましたが、失敗しました。\n"
							+ "`" + failure.getClass().getName() + "` -> `" + failure.getMessage() + "`");
				});
			} else if (!isConnected) {
				// 連携されていない。(MinecraftConnectedロールも付与されていない)
				// -> VerifiedとRegularの付与確認だけして終了
				removePermissionRoles(member);
				return;
			}
			// MinecraftConnectedロールが付与されていて、連携されている。
			// -> 続行

			// 以降、連携済み。

			// check changed name
			String db_name = res.getString("name");
			if (!db_name.equals(member.getUser().getName())) {
				// changed
				System.out.println("[Task_AccountConnectChecker] " + member.getUser().getAsTag()
						+ ": Changed name (" + db_name + " -> " + member.getUser().getName() + ")");
				PreparedStatement stmt = conn
						.prepareStatement("UPDATE discordlink SET name = ? WHERE disid = ? AND disabled = ?");
				stmt.setString(1, member.getUser().getName());
				stmt.setString(2, member.getId());
				stmt.setBoolean(3, false);
				stmt.executeUpdate();
				stmt.close();
			}

			// check discriminator
			String db_discriminator = res.getString("discriminator");
			if (!db_discriminator.equals(member.getUser().getDiscriminator())) {
				// changed
				System.out.println("[Task_AccountConnectChecker] " + member.getUser().getAsTag()
						+ ": Changed discriminator (" + db_discriminator + " -> "
						+ member.getUser().getDiscriminator()
						+ ")");
				PreparedStatement stmt = conn
						.prepareStatement(
								"UPDATE discordlink SET discriminator = ? WHERE disid = ? AND disabled = ?");
				stmt.setString(1, member.getUser().getDiscriminator());
				stmt.setString(2, member.getId());
				stmt.setBoolean(3, false);
				stmt.executeUpdate();
				stmt.close();
			}

			// check changed playername
			String db_player = res.getString("player");
			UUID db_uuid = UUID.fromString(res.getString("uuid"));
			String now_player = getLatestMinecraftID(db_uuid);
			if (now_player != null && !db_player.equals(now_player)) {
				// changed
				System.out.println("[Task_AccountConnectChecker] " + member.getUser().getAsTag()
						+ ": Changed playername (" + db_player + " -> " + now_player + ")");
				PreparedStatement stmt = conn
						.prepareStatement("UPDATE discordlink SET player = ? WHERE disid = ? AND disabled = ?");
				stmt.setString(1, now_player);
				stmt.setString(2, member.getId());
				stmt.setBoolean(3, false);
				stmt.executeUpdate();
				stmt.close();
			}

			// check changed permission group
			String db_group = res.getString("pex");
			String group = getPermissionGroup(db_uuid);
			if (group != null && !db_group.equals(group)) {
				// changed
				System.out.println("[Task_AccountConnectChecker] " + member.getUser().getAsTag()
						+ ": Changed permission group (" + db_group + " -> " + group + ")");
				PreparedStatement stmt = conn
						.prepareStatement("UPDATE discordlink SET pex = ? WHERE disid = ? AND disabled = ?");
				stmt.setString(1, group);
				stmt.setString(2, member.getId());
				stmt.setBoolean(3, false);
				stmt.executeUpdate();
				stmt.close();
			}

			res.close();
			statement.close();

			// 権限グループのチェック及び妥当なロールの設定
			boolean isVerified = member.getRoles().stream()
					.filter(role -> role != null && role.getIdLong() == Verified.getIdLong())
					.count() != 0;
			boolean isRegular = member.getRoles().stream()
					.filter(role -> role != null && role.getIdLong() == Regular.getIdLong())
					.count() != 0;
			if (!isVerified && group.equalsIgnoreCase("Verified")) {
				// Verified
				removePermissionRoles(member);
				guild.addRoleToMember(member, Verified).queue(v -> {
					notify("`" + member.getUser().getAsTag() + "`: Verifiedロールを設定しました。 (" + group + ")");
				}, failure -> {
					notify("`" + member.getUser().getAsTag() + "`: Verifiedロールを設定しようとしましたが、失敗しました。\n"
							+ "`" + failure.getClass().getName() + "` -> `" + failure.getMessage() + "`");
				});
			} else if (!isRegular && group.equalsIgnoreCase("Regular")) {
				// Regular
				removePermissionRoles(member);
				guild.addRoleToMember(member, Regular).queue(v -> {
					notify("`" + member.getUser().getAsTag() + "`: Regularロールを設定しました。 (" + group + ")");
				}, failure -> {
					notify("`" + member.getUser().getAsTag() + "`: Regularロールを設定しようとしましたが、失敗しました。\n"
							+ "`" + failure.getClass().getName() + "` -> `" + failure.getMessage() + "`");
				});
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 「Guildに所属していなくて連携されている」かどうかのチェック及びその場合の処理
	 * @param member
	 */
	private void processLinkedAndLeaved(List<Member> members) {
		if (members.isEmpty()) {
			return;
		}
		// Guildメンバーのidセット
		Set<String> membersIds = members.stream().map(member -> member.getId()).collect(Collectors.toSet());
		// 連携済みアカウントのidセット
		Set<String> linkedMembers = new HashSet<>();
		Map<String, String> discordTagMap = new HashMap<>();
		try {
			MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM discordlink WHERE disabled = ?");
			statement.setBoolean(1, false);
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				linkedMembers.add(res.getString("disid"));
				discordTagMap.put(res.getString("disid"), res.getString("name") + "#" + res.getString("discriminator"));
			}
			res.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		// 連携されている(DBにある)のに、サーバにいない。
		Set<String> leavedLinkingIds = linkedMembers.stream().filter(id -> !membersIds.contains(id))
				.collect(Collectors.toSet());
		if (leavedLinkingIds.isEmpty()) {
			return;
		}
		try {
			for (String id : leavedLinkingIds) {
				MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
				Connection conn = MySQLDBManager.getConnection();
				PreparedStatement statement = conn
						.prepareStatement("UPDATE discordlink SET disabled = ? WHERE disid = ?");
				statement.setBoolean(1, false);
				statement.setString(2, id);
				statement.executeUpdate();
				statement.close();

				String discordTag = discordTagMap.get(id);
				notify("`" + discordTag + "`: jMS Gamers Clubから退出したため、連携を解除しました。");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	private String getLatestMinecraftID(UUID uuid) {
		try {
			MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM login WHERE uuid = ? ORDER BY id DESC LIMIT 1");
			statement.setString(1, uuid.toString());
			ResultSet res = statement.executeQuery();
			if (!res.next()) {
				return null;
			}
			return res.getString("player");
		} catch (SQLException e) {
			return null;
		}
	}

	private String getPermissionGroup(UUID uuid) {
		// from API
		try {
			String url = "https://api.jaoafa.com/users/" + uuid.toString();
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(url).get().build();
			Response response = client.newCall(request).execute();
			if (response.code() != 200) {
				return null;
			}
			JSONObject json = new JSONObject(response.body().string());
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

			return data.optString("permission");
		} catch (IOException e) {
			System.out
					.println("[Task_AccountConnectChecker|getPermissionGroup] Throwed IOException: " + e.getMessage());
			return null;
		}
	}

	/**
	 * VerifiedとRegularロールを解除します。
	 * @param member
	 */
	private void removePermissionRoles(Member member) {
		boolean isVerified = member.getRoles().stream()
				.filter(role -> role != null && role.getIdLong() == Verified.getIdLong())
				.count() != 0;
		boolean isRegular = member.getRoles().stream()
				.filter(role -> role != null && role.getIdLong() == Regular.getIdLong())
				.count() != 0;

		if (isVerified) {
			guild.removeRoleFromMember(member, Verified).queue(v -> {
				notify("`" + member.getUser().getAsTag() + "`: Verifiedロールを外しました。");
			}, failure -> {
				notify("`" + member.getUser().getAsTag() + "`: Verifiedロールを外そうとしましたが、失敗しました。\n"
						+ "`" + failure.getClass().getName() + "` -> `" + failure.getMessage() + "`");
			});
		}
		if (isRegular) {
			guild.removeRoleFromMember(member, Regular).queue(v -> {
				notify("`" + member.getUser().getAsTag() + "`: Regularロールを外しました。");
			}, failure -> {
				notify("`" + member.getUser().getAsTag() + "`: Regularロールを外そうとしましたが、失敗しました。\n"
						+ "`" + failure.getClass().getName() + "` -> `" + failure.getMessage() + "`");
			});
		}
	}

	private void notify(String message) {
		channel.sendMessage(message).queue();
	}
}
