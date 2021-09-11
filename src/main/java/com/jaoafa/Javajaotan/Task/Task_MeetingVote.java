package com.jaoafa.Javajaotan.Task;

import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Task_MeetingVote extends TimerTask {
    int TeamJaoCount = 9; // Admin + Moderator
    Pattern p = Pattern.compile("\\[Border:([0-9]+)]");
    boolean debugMode = false;

    public Task_MeetingVote() {

    }

    public Task_MeetingVote(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public void run() {
        System.out.println("Task_MeetingVote().run()");
        JDA jda = Main.getJDA();

        double divided = TeamJaoCount / 2;
        int VoteBorder = (int) Math.ceil(divided);
        if (TeamJaoCount % 2 == 0) {
            VoteBorder++;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        TextChannel channel = jda.getTextChannelById(597423974816808970L);
        if (channel == null) {
            System.out.println("getTextChannelById(#meeting_vote) == null.");
            return;
        }
        List<Message> messages = channel.retrievePinnedMessages().complete();
        for (Message message : messages) {
            String content = message.getContentDisplay();
            OffsetDateTime timestamp = message.getTimeCreated().withOffsetSameInstant(ZoneOffset.ofHours(9));

            List<User> good = message.retrieveReactionUsers("\uD83D\uDC4D").complete();
            int good_count = good.size();
            List<User> bad = message.retrieveReactionUsers("\uD83D\uDC4E").complete();
            int bad_count = bad.size();
            List<User> white = message.retrieveReactionUsers("🏳️").complete();
            int white_count = white.size();

            int _VoteBorder = VoteBorder;
            if (content.contains("\n")) {
                String[] contents = content.split("\n");
                Matcher m = p.matcher(contents[0]);
                if (m.find()) {
                    if (!Library.isInt(m.group(1))) {
                        continue;
                    }
                    _VoteBorder = Integer.parseInt(m.group(1));
                }
            }
            int _white = white_count;
            if (_white != 0) {
                if (_VoteBorder % 2 == 0) {
                    _VoteBorder--;
                    _white--;
                }
                _VoteBorder -= _white / 2;
            }
            if (debugMode) {
                System.out.println("MeetingVote[debugMode] " + message.getId() + " by "
                        + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator());
                System.out.println("MeetingVote[debugMode] " + "VoteBorder: " + VoteBorder + " / Good: " + good_count
                        + " / Bad: " + bad_count + " / White: " + white_count + " / _VoteBorder: " + _VoteBorder);
            }

            if (good_count >= _VoteBorder) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("VOTE RESULT");
                builder.setDescription("@here :thumbsup:投票が承認されたことをお知らせします。");
                builder.addField("賛成 / 反対 / 白票", good_count + " / " + bad_count + " / " + white_count, false);
                builder.addField("決議ボーダー", String.valueOf(_VoteBorder), false);
                builder.addField("メンバー", "賛成: " + good.stream().map(User::getAsTag).collect(Collectors.joining(", ")) + "\n" +
                    "反対: " + bad.stream().map(User::getAsTag).collect(Collectors.joining(", ")), false);
                builder.addField("内容", content, false);
                builder.addField("対象メッセージURL", "https://discord.com/channels/" + message.getGuild().getId()
                    + "/" + message.getChannel().getId() + "/" + message.getId(), false);
                builder.addField("投票開始日時", dtf.format(timestamp), false);
                builder.setColor(Color.GREEN);
                channel.sendMessage(builder.build()).queue();
                message.unpin().queue();

                autoGoodCitiesRequest(message);
            } else if (bad_count >= _VoteBorder) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("VOTE RESULT");
                builder.setDescription("@here :thumbsdown:投票が否認されたことをお知らせします。");
                builder.addField("賛成 / 反対 / 白票", good_count + " / " + bad_count + " / " + white_count, false);
                builder.addField("決議ボーダー", String.valueOf(_VoteBorder), false);
                builder.addField("メンバー", "賛成: " + good.stream().map(User::getAsTag).collect(Collectors.joining(", ")) + "\n" +
                    "反対: " + bad.stream().map(User::getAsTag).collect(Collectors.joining(", ")), false);
                builder.addField("内容", content, false);
                builder.addField("対象メッセージURL", "https://discord.com/channels/" + message.getGuild().getId()
                    + "/" + message.getChannel().getId() + "/" + message.getId(), false);
                builder.addField("投票開始日時", dtf.format(timestamp), false);
                builder.setColor(Color.RED);
                channel.sendMessage(builder.build()).queue();
                message.unpin().queue();

                autoBadCitiesRequest(message);
            }

            long start = timestamp.toEpochSecond();
            long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(TimeUnit.SECONDS.toMillis(start));
            cal.add(Calendar.WEEK_OF_YEAR, 2);

            if ((start + 604800) <= now) {
                // 1週間 604800秒
                long isRemind = message.retrieveReactionUsers("📳").complete().stream()
                        .filter(_user -> _user != null && _user.getIdLong() == jda.getSelfUser().getIdLong()).count();
                if (isRemind == 0) {
                    List<Long> admin_and_moderator = Arrays.asList(206692134991036416L, // zakuro
                            221498004505362433L, // hiratake
                            221991565567066112L, // tomachi
                            222337959087702016L, // omelet
                            189377054955798528L, // ekusas
                            189372008147058688L, // zokasu
                            315726390844719114L, // kohona
                            290787709721509890L, // nudonge
                            310570792691826688L // ekp
                    );

                    List<Long> goodUsers = good.stream().map(ISnowflake::getIdLong).collect(Collectors.toList());
                    List<Long> badUsers = bad.stream().map(ISnowflake::getIdLong).collect(Collectors.toList());
                    List<Long> whiteUsers = white.stream().map(ISnowflake::getIdLong).collect(Collectors.toList());

                    List<String> mentions = admin_and_moderator.stream()
                            .filter(_userid -> !goodUsers.contains(_userid) && !badUsers.contains(_userid)
                                    && !whiteUsers.contains(_userid))
                            .map(_userid -> "<@" + _userid + ">").collect(Collectors.toList());

                    channel.sendMessage("__**投票有効期限が1週間を切った投票があります。投票をお願いします。**__\n"
                            + "**未投票者**: " + String.join(", ", mentions) + "\n"
                            + "**投票有効期限**: " + sdf.format(cal.getTime()) + "\n"
                            + "**メッセージURL**: https://discord.com/channels/" + message.getGuild().getId() + "/"
                            + message.getChannel().getId() + "/" + message.getId()).queue();
                    message.addReaction("📳").queue();
                }
            }

            if ((start + 1209600) <= now) {
                // 2週間 1209600秒
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("VOTE RESULT");
                builder.setDescription("@here :wave:有効会議期限を過ぎたため、投票が否認されたことをお知らせします。");
                builder.addField("賛成 / 反対 / 白票", good_count + " / " + bad_count + " / " + white_count, false);
                builder.addField("決議ボーダー", String.valueOf(_VoteBorder), false);
                builder.addField("メンバー", "賛成: " + good.stream().map(User::getAsTag).collect(Collectors.joining(", ")) + "\n" +
                    "反対: " + bad.stream().map(User::getAsTag).collect(Collectors.joining(", ")), false);
                builder.addField("内容", content, false);
                builder.addField("対象メッセージURL", "https://discord.com/channels/" + message.getGuild().getId()
                    + "/" + message.getChannel().getId() + "/" + message.getId(), false);
                builder.addField("投票開始日時",
                    dtf.format(timestamp) + " (" + timestamp.toEpochSecond() + ")",
                    false);
                builder.addField("有効会議期限",
                    sdf.format(cal.getTime()) + " (" + TimeUnit.MILLISECONDS.toSeconds(cal.getTimeInMillis()) + ")",
                    false);
                builder.addField("現在時刻",
                        sdf.format(new Date(TimeUnit.SECONDS.toMillis(now))) + " (" + now + ")",
                        false);
                builder.setColor(Color.ORANGE);
                channel.sendMessage(builder.build()).queue();
                message.unpin().queue();

                autoBadCitiesRequest(message);
            }
        }
    }

    private void autoGoodCitiesRequest(Message message) {
        String contents = message.getContentRaw();
        if (!contents.startsWith("[API-CITIES-")) {
            return;
        }

        autoGood_CREATE_WAITING(contents);
        autoGood_CHANGE_CORNERS(contents);
        autoGood_CHANGE_OTHER(contents);
    }

    private void autoGood_CREATE_WAITING(String contents) {
        System.out.println("autoGood_CREATE_WAITING()");
        Pattern p = Pattern.compile("\\[API-CITIES-CREATE-WAITING:([0-9]+)]");
        Matcher m = p.matcher(contents);
        if (!m.find()) {
            System.out.println("autoGood_CREATE_WAITING(): m.find false");
            return;
        }

        int id = Integer.parseInt(m.group(1));

        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            System.out.println("autoGood_CREATE_WAITING(): MySQLDBManager null");
            return;
        }

        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities_new_waiting WHERE id = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                System.out.println("autoGood_CREATE_WAITING(): res.next false");
                return;
            }

            int reqid = res.getInt("id");
            String cities_name = res.getString("name");
            String regionName = res.getString("regionname");
            String regionOwner = res.getString("player");
            JSONArray corners = new JSONArray(res.getString("corners"));
            res.close();
            statement.close();

            List<String> approvalflowBuilder = new LinkedList<>();
            approvalflowBuilder.add("サーバにログインします。");
            approvalflowBuilder.add("鯖内でコマンドを実行: `//sel poly`");
            for (int i = 0; i < corners.length(); i++) {
                JSONObject corner = corners.getJSONObject(i);
                approvalflowBuilder
                        .add("鯖内でコマンドを実行: `/tp " + corner.getInt("x") + " 100 " + corner.getInt("z") + "`");
                if (i == 0) {
                    approvalflowBuilder.add("鯖内でコマンドを実行: `//pos1`");
                } else {
                    approvalflowBuilder.add("鯖内でコマンドを実行: `//pos2`");
                }
            }
            approvalflowBuilder.add("鯖内でコマンドを実行: `//expand vert`");
            approvalflowBuilder.add("鯖内でコマンドを実行: `/rg define " + regionName + " " + regionOwner + "`");
            approvalflowBuilder.add("<#597423467796758529>内でコマンド「`/approvalcity create " + reqid + "`」を実行してください。");

            List<String> approvalflows = new LinkedList<>();
            int i = 1;
            for (String str : approvalflowBuilder) {
                approvalflows.add(i + ". " + str);
                i++;
            }

            TextChannel meeting = Main.getJDA().getTextChannelById(597423467796758529L);
            if (meeting == null) {
                System.out.println("[autoGood_CHANGE_CORNERS] getTextChannelById(#meeting) == null");
                return;
            }
            meeting.sendMessage(
                    "**自治体「`" + cities_name + "`」の新規登録申請が承認されました。これに伴い、運営利用者は以下の作業を順に実施してください。**\n"
                            + String.join("\n", approvalflows))
                    .queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void autoGood_CHANGE_CORNERS(String contents) {
        System.out.println("autoGood_CHANGE_CORNERS()");
        Pattern p = Pattern.compile("\\[API-CITIES-CHANGE-CORNERS-WAITING:([0-9]+)]");
        Matcher m = p.matcher(contents);
        if (!m.find()) {
            System.out.println("autoGood_CHANGE_CORNERS(): m.find false");
            return;
        }

        int id = Integer.parseInt(m.group(1));

        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            System.out.println("autoGood_CHANGE_CORNERS(): MySQLDBManager null");
            return;
        }

        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities_corners_waiting WHERE id = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                System.out.println("autoGood_CHANGE_CORNERS(): res.next false");
                res.close();
                statement.close();
                return;
            }

            int reqid = res.getInt("id");
            int cities_id = res.getInt("cities_id");
            JSONArray corners = new JSONArray(res.getString("corners_new"));
            res.close();
            statement.close();

            String cities_name = getCitiesName(conn, cities_id);
            String regionName = getRegionName(conn, cities_id);

            List<String> approvalflowBuilder = new LinkedList<>();
            approvalflowBuilder.add("サーバにログインします。");
            approvalflowBuilder.add("鯖内でコマンドを実行: `//sel poly`");
            for (int i = 0; i < corners.length(); i++) {
                JSONObject corner = corners.getJSONObject(i);
                approvalflowBuilder
                        .add("鯖内でコマンドを実行: `/tp " + corner.getInt("x") + " 100 " + corner.getInt("z") + "`");
                if (i == 0) {
                    approvalflowBuilder.add("鯖内でコマンドを実行: `//pos1`");
                } else {
                    approvalflowBuilder.add("鯖内でコマンドを実行: `//pos2`");
                }
            }
            approvalflowBuilder.add("鯖内でコマンドを実行: `//expand vert`");
            approvalflowBuilder.add("鯖内でコマンドを実行: `/rg redefine " + regionName + "`");
            approvalflowBuilder.add("<#597423467796758529>内でコマンド「`/approvalcity corners " + reqid + "`」を実行してください。");

            List<String> approvalflows = new LinkedList<>();
            int i = 1;
            for (String str : approvalflowBuilder) {
                approvalflows.add(i + ". " + str);
                i++;
            }

            TextChannel meeting = Main.getJDA().getTextChannelById(597423467796758529L);
            if (meeting == null) {
                System.out.println("[autoGood_CHANGE_CORNERS] getTextChannelById(#meeting) == null");
                return;
            }
            meeting.sendMessage(
                    "**自治体「`" + cities_name + "`」の範囲変更申請が承認されました。これに伴い、運営利用者は以下の作業を順に実施してください。**\n"
                            + String.join("\n", approvalflows))
                    .queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void autoGood_CHANGE_OTHER(String contents) {
        System.out.println("autoGood_CHANGE_OTHER()");
        Pattern p = Pattern.compile("\\[API-CITIES-CHANGE-OTHER-WAITING:([0-9]+)]");
        Matcher m = p.matcher(contents);
        if (!m.find()) {
            System.out.println("autoGood_CHANGE_OTHER(): m.find false");
            return;
        }

        int id = Integer.parseInt(m.group(1));

        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            System.out.println("autoGood_CHANGE_OTHER(): MySQLDBManager null");
            return;
        }

        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities_other_waiting WHERE id = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                System.out.println("autoGood_CHANGE_OTHER(): res.next false");
                res.close();
                statement.close();
                return;
            }

            int reqid = res.getInt("id");
            int cities_id = res.getInt("cities_id");

            LinkedList<String> pre_sql = new LinkedList<>();
            LinkedList<String> setStrings = new LinkedList<>();

            String[] keys = new String[]{
                    "name",
                    "namekana",
                    "regionname",
                    "summary",
                    "name_origin"
            };
            for (String key : keys) {
                if (res.getString(key + "_new") == null) {
                    continue;
                }
                pre_sql.add(key + " = ?");
                setStrings.add(res.getString(key + "_new"));
            }

            res.close();
            statement.close();

            PreparedStatement statement_cities_update = conn
                    .prepareStatement("UPDATE cities SET " + String.join(", ", pre_sql) + " WHERE id = ?");
            int i = 1;
            for (String str : setStrings) {
                statement_cities_update.setString(i, str);
                i++;
            }
            statement_cities_update.setInt(i, cities_id);
            System.out.println("SQL: " + statement_cities_update);
            statement_cities_update.executeUpdate();

            String discord_userid = getDiscordUserID(conn, cities_id);
            String cities_name = getCitiesName(conn, cities_id);

            TextChannel city_request = Main.getJDA().getTextChannelById(709008822043148340L);
            if (city_request == null) {
                System.out.println("[autoGood_CHANGE_OTHER] getTextChannelById(#city_request) == null");
                return;
            }
            city_request.sendMessage("<@" + discord_userid + "> 自治体「`"
                    + cities_name + "` (" + cities_id + ")」の自治体情報変更申請を**承認**しました。(リクエストID: " + reqid + ")").queue();

            PreparedStatement statement_update = conn
                    .prepareStatement("UPDATE cities_other_waiting SET status = ? WHERE id = ?");
            statement_update.setInt(1, 1);
            statement_update.setInt(2, id);
            statement_update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------- //

    private void autoBadCitiesRequest(Message message) {
        String contents = message.getContentRaw();
        if (!contents.startsWith("[API-CITIES-")) {
            return;
        }

        autoBad_CREATE_WAITING(contents);
        autoBad_CHANGE_CORNERS(contents);
        autoBad_CHANGE_OTHER(contents);
    }

    private void autoBad_CREATE_WAITING(String contents) {
        System.out.println("autoBad_CREATE_WAITING()");
        Pattern p = Pattern.compile("\\[API-CITIES-CREATE-WAITING:([0-9]+)]");
        Matcher m = p.matcher(contents);
        if (!m.find()) {
            System.out.println("autoBad_CREATE_WAITING(): m.find false");
            return;
        }

        int id = Integer.parseInt(m.group(1));

        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            System.out.println("autoBad_CREATE_WAITING(): MySQLDBManager null");
            return;
        }

        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities_new_waiting WHERE id = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                System.out.println("autoBad_CREATE_WAITING(): res.next false");
                return;
            }

            int reqid = res.getInt("id");
            String discord_userid = res.getString("discord_userid");
            String cities_name = res.getString("name");
            res.close();
            statement.close();

            TextChannel city_request = Main.getJDA().getTextChannelById(709008822043148340L);
            if (city_request == null) {
                System.out.println("[autoBad_CREATE_WAITING] getTextChannelById(#city_request) == null");
                return;
            }
            city_request.sendMessage("<@" + discord_userid + "> 自治体「`"
                    + cities_name + "`」の自治体新規登録申請を**否認**しました。(リクエストID: " + reqid + ")").queue();

            PreparedStatement statement_update = conn
                    .prepareStatement("UPDATE cities_new_waiting SET status = ? WHERE id = ?");
            statement_update.setInt(1, -1);
            statement_update.setInt(2, id);
            statement_update.executeUpdate();
            statement_update.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void autoBad_CHANGE_CORNERS(String contents) {
        System.out.println("autoBad_CHANGE_CORNERS()");
        Pattern p = Pattern.compile("\\[API-CITIES-CHANGE-CORNERS-WAITING:([0-9]+)]");
        Matcher m = p.matcher(contents);
        if (!m.find()) {
            System.out.println("autoBad_CHANGE_CORNERS(): m.find false");
            return;
        }

        int id = Integer.parseInt(m.group(1));

        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            System.out.println("autoBad_CHANGE_CORNERS(): MySQLDBManager null");
            return;
        }

        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities_corners_waiting WHERE id = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                System.out.println("autoBad_CHANGE_CORNERS(): res.next false");
                return;
            }

            int reqid = res.getInt("id");
            int cities_id = res.getInt("cities_id");
            res.close();
            statement.close();

            String discord_userid = getDiscordUserID(conn, cities_id);
            String cities_name = getCitiesName(conn, cities_id);

            TextChannel city_request = Main.getJDA().getTextChannelById(709008822043148340L);
            if (city_request == null) {
                System.out.println("[autoBad_CHANGE_CORNERS] getTextChannelById(#city_request) == null");
                return;
            }
            city_request.sendMessage("<@" + discord_userid + "> 自治体「`"
                    + cities_name + "` (" + cities_id + ")」の自治体範囲変更申請を**否認**しました。(リクエストID: " + reqid + ")").queue();

            PreparedStatement statement_update = conn
                    .prepareStatement("UPDATE cities_corners_waiting SET status = ? WHERE id = ?");
            statement_update.setInt(1, -1);
            statement_update.setInt(2, id);
            statement_update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void autoBad_CHANGE_OTHER(String contents) {
        System.out.println("autoBad_CHANGE_OTHER()");
        Pattern p = Pattern.compile("\\[API-CITIES-CHANGE-OTHER-WAITING:([0-9]+)]");
        Matcher m = p.matcher(contents);
        if (!m.find()) {
            System.out.println("autoBad_CHANGE_OTHER(): m.find false");
            return;
        }

        int id = Integer.parseInt(m.group(1));

        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
        if (MySQLDBManager == null) {
            System.out.println("autoBad_CHANGE_OTHER(): MySQLDBManager null");
            return;
        }

        try {
            Connection conn = MySQLDBManager.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities_other_waiting WHERE id = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                System.out.println("autoBad_CHANGE_OTHER(): res.next false");
                return;
            }

            int reqid = res.getInt("id");
            int cities_id = res.getInt("cities_id");
            res.close();
            statement.close();

            String discord_userid = getDiscordUserID(conn, cities_id);
            String cities_name = getCitiesName(conn, cities_id);

            TextChannel city_request = Main.getJDA().getTextChannelById(709008822043148340L);
            if (city_request == null) {
                System.out.println("[autoBad_CHANGE_OTHER] getTextChannelById(#city_request) == null");
                return;
            }
            city_request.sendMessage("<@" + discord_userid + "> 自治体「`"
                    + cities_name + "` (" + cities_id + ")」の自治体情報変更申請を**否認**しました。(リクエストID: " + reqid + ")").queue();

            PreparedStatement statement_update = conn
                    .prepareStatement("UPDATE cities_other_waiting SET status = ? WHERE id = ?");
            statement_update.setInt(1, -1);
            statement_update.setInt(2, id);
            statement_update.executeUpdate();
            statement_update.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getDiscordUserID(Connection conn, int cities_id) {
        try {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities WHERE id = ?");
            statement.setInt(1, cities_id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                res.close();
                statement.close();
                return null;
            }

            String discorduserid = res.getString("discord_userid");
            res.close();
            statement.close();
            return discorduserid;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getCitiesName(Connection conn, int cities_id) {
        try {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities WHERE id = ?");
            statement.setInt(1, cities_id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                res.close();
                statement.close();
                return null;
            }

            String discorduserid = res.getString("name");
            res.close();
            statement.close();
            return discorduserid;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getRegionName(Connection conn, int cities_id) {
        try {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM cities WHERE id = ?");
            statement.setInt(1, cities_id);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                res.close();
                statement.close();
                return null;
            }

            String region_name = res.getString("regionname");
            res.close();
            statement.close();
            return region_name;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
