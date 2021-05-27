package com.jaoafa.Javajaotan;

import com.jaoafa.Javajaotan.ALLChat.ALLChatMainEvent;
import com.jaoafa.Javajaotan.Channel.ChannelMainEvent;
import com.jaoafa.Javajaotan.Command.MessageMainEvent;
import com.jaoafa.Javajaotan.Event.*;
import com.jaoafa.Javajaotan.Lib.ChatManager;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Lib.PriconeCharacter;
import com.jaoafa.Javajaotan.Task.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.*;

public class Main {
    public static TextChannel ReportChannel = null;
    public static MySQLDBManager MySQLDBManager = null;
    public static String translateGAS = null;
    public static String originAPIUrl = null;
    public static List<PriconeCharacter> pricone_Characters = new ArrayList<>();
    private static JDA jda = null;
    private static ChatManager chatManager = null;

    public static void main(String[] args) {
        File f = new File("conf.properties");
        Properties props;
        try {
            InputStream is = new FileInputStream(f);

            // プロパティファイルを読み込む
            props = new Properties();
            props.load(is);
        } catch (FileNotFoundException e) {
            // ファイル生成
            props = new Properties();
            props.setProperty("token", "PLEASETOKEN");
            props.setProperty("sqlserver", "PLEASE");
            props.setProperty("sqluser", "PLEASE");
            props.setProperty("sqlpassword", "PLEASE");
            props.setProperty("translateGAS", "PLEASE");
            props.setProperty("originAPIUrl", "PLEASE");
            try {
                props.store(new FileOutputStream("conf.properties"), "Comments");
                System.out.println("Please Config Token!");
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // キーを指定して値を取得する
        String token = props.getProperty("token");
        if (token.equalsIgnoreCase("PLEASETOKEN")) {
            System.out.println("Please Token!");
            return;
        }
        String sqlserver = props.getProperty("sqlserver");
        if (sqlserver.equalsIgnoreCase("PLEASE")) {
            System.out.println("Please Token!");
            return;
        }
        String sqluser = props.getProperty("sqluser");
        if (sqluser.equalsIgnoreCase("PLEASE")) {
            System.out.println("Please Token!");
            return;
        }
        String sqlpassword = props.getProperty("sqlpassword");
        if (sqlpassword.equalsIgnoreCase("PLEASE")) {
            System.out.println("Please Token!");
            return;
        }
        translateGAS = props.getProperty("translateGAS");
        if (translateGAS.equalsIgnoreCase("PLEASE")) {
            translateGAS = null;
        }
        originAPIUrl = props.getProperty("originAPIUrl");
        if (translateGAS.equalsIgnoreCase("PLEASE")) {
            translateGAS = null;
        }
        try {
            MySQLDBManager = new MySQLDBManager(sqlserver, "3306", "jaoafa", sqluser, sqlpassword);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String nobyAPIKey = props.getProperty("nobyAPIKey");
        if (nobyAPIKey.equalsIgnoreCase("PLEASETOKEN")) {
            System.out.println("Please nobyAPIKey!");
            return;
        }

        String userlocalAPIKey = props.getProperty("userlocalAPIKey");
        if (userlocalAPIKey.equalsIgnoreCase("PLEASETOKEN")) {
            System.out.println("Please userlocalAPIKey!");
            return;
        }

        String A3RTAPIKey = props.getProperty("A3RTAPIKey");
        if (A3RTAPIKey.equalsIgnoreCase("PLEASETOKEN")) {
            System.out.println("Please A3RTAPIKey!");
            return;
        }

        String ChaplusAPIKey = props.getProperty("ChaplusAPIKey");
        if (ChaplusAPIKey.equalsIgnoreCase("PLEASETOKEN")) {
            System.out.println("Please ChaplusAPIKey!");
            return;
        }
        chatManager = new ChatManager(nobyAPIKey, userlocalAPIKey, A3RTAPIKey, ChaplusAPIKey);

        // Javajaotan2移行対応

        // 分けてイベント自動登録できるように？
        // 全部JDA移行
        try {
            JDABuilder jdabuilder = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES,
                    GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING)
                .setAutoReconnect(true)
                .setBulkDeleteSplittingEnabled(false)
                .setContextEnabled(false)
                    .setEventManager(new AnnotatedEventManager());

            jdabuilder.addEventListeners(new MessageMainEvent());
            jdabuilder.addEventListeners(new ChannelMainEvent());
            jdabuilder.addEventListeners(new ALLChatMainEvent());
            jdabuilder.addEventListeners(new Event_ServerJoin());
            jdabuilder.addEventListeners(new Event_ServerLeave());
            jdabuilder.addEventListeners(new Event_ServerBanned());
            jdabuilder.addEventListeners(new Event_ReactionAddEvent());
            jdabuilder.addEventListeners(new Event_TodoCheck());
            jdabuilder.addEventListeners(new Event_TomachiEmojis());

            jda = jdabuilder.build().awaitReady();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
		/*
		IDiscordClient client = createClient(token, true);
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new MessageMainEvent());
		dispatcher.registerListener(new ChannelMainEvent());
		dispatcher.registerListener(new ALLChatMainEvent());
		dispatcher.registerListener(new Event_ServerJoin());
		dispatcher.registerListener(new Event_ServerLeave());
		dispatcher.registerListener(new Event_ServerBanned());
		dispatcher.registerListener(new Event_MessageReceived());
		dispatcher.registerListener(new Event_ReactionAddEvent());
		dispatcher.registerListener(new Event_TodoCheck());
		
		try {
			jda = new JDABuilder(AccountType.BOT)
					.setAudioEnabled(false)
					.setAutoReconnect(true)
					.setBulkDeleteSplittingEnabled(false)
					.setToken(token)
					.setContextEnabled(false)
					.build().awaitReady();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}*/

        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        () -> System.out.println("Exit")));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Task_VerifiedCheck(), 10000L, 60000L); // 1分
        timer.scheduleAtFixedRate(new Task_MeetingVote(), 20000L, 600000L); // 10分
        timer.scheduleAtFixedRate(new Task_AccountConnectChecker(), 30000L, 600000L); // 10分
        timer.scheduleAtFixedRate(new Task_MinecraftConnectedCheck(), 40000L, 1800000L); // 30分
        timer.scheduleAtFixedRate(new Task_SubAccountCheck(), 50000L, 1800000L); // 30分
        timer.scheduleAtFixedRate(new Task_3MonthCheck(), 60000L, 1800000L);
		/*
		JavajaotanWatcher JavajaotanWatcher = new JavajaotanWatcher();
		Timer timer = new Timer();
		timer.schedule(JavajaotanWatcher, 60000);
		*/
    }

    public static JDA getJDA() {
        return jda;
    }

    public static void setJDA(JDA jda) {
        Main.jda = jda;
    }

    public static void DiscordExceptionError(@NotNull Class<?> clazz, @Nullable Message message,
                                             @NotNull Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.flush();
        InputStream is = new ByteArrayInputStream(sw.toString().getBytes(StandardCharsets.UTF_8));
        if (message != null) {
            message.reply(":pencil:おっと！Javajaotanでなにか問題が発生したようです！ <@221991565567066112>\n**ErrorMsg**: `"
                    + exception.getMessage()
                    + "`\n**Class**: `" + clazz.getName() + " (" + exception.getClass().getName() + ")`").queue();
        } else {
            Main.ReportChannel.sendMessage(":pencil:おっと！Javajaotanでなにか問題が発生したようです！ <@221991565567066112>\n**ErrorMsg**: `"
                    + exception.getMessage()
                    + "`\n**Class**: `" + clazz.getName() + " (" + exception.getClass().getName() + ")`").queue();
        }
        Main.ReportChannel.sendFile(is, "stacktrace.txt").queue();
    }

    public static void DiscordExceptionError(@NotNull Class<?> clazz, @NotNull MessageChannel channel,
                                             @NotNull Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.flush();
        InputStream is = new ByteArrayInputStream(sw.toString().getBytes(StandardCharsets.UTF_8));
        channel.sendMessage(":pencil:おっと！Javajaotanでなにか問題が発生したようです！ <@221991565567066112>\n**ErrorMsg**: `"
                + exception.getMessage()
                + "`\n**Class**: `" + clazz.getName() + " (" + exception.getClass().getName() + ")`").queue();
        Main.ReportChannel.sendFile(is, "stacktrace.txt").queue();
    }

    public static void ExceptionReporter(@Nullable Message message, @NotNull Throwable exception) {
        if (message != null) {
            message.reply(
                    ":pencil:おっと！Javajaotanでなにか問題が発生したようです！ <@221991565567066112>\n**Throwable Class**: `"
                            + exception.getClass().getName() + "`")
                    .queue();
        }
        if (Main.ReportChannel == null) {
            System.out.println("ExceptionReporter: Javajaotan.ReportChannel == null.");
            System.out.println("ExceptionReporter did not work properly!");
            return;
        }
        exception.printStackTrace();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("javajaotan Error Reporter");
            builder.setColor(Color.RED);
            builder.addField("StackTrace", "```" + sw + "```", false);
            builder.addField("Message", "```" + exception.getMessage() + "```", false);
            builder.addField("Cause", "```" + exception.getCause() + "```", false);
            builder.setTimestamp(Instant.now());
            Main.ReportChannel.sendMessage(builder.build()).queue();
        } catch (Exception e) {
            String text = "javajaotan Error Reporter (" + Library.sdfFormat(new Date()) + ")\n"
                + "---------- StackTrace ----------\n"
                + sw + "\n"
                + "---------- Message ----------\n"
                + exception.getMessage() + "\n"
                + "---------- Cause ----------\n"
                + exception.getCause();
            InputStream stream = new ByteArrayInputStream(
                text.getBytes(StandardCharsets.UTF_8));
            Main.ReportChannel.sendFile(stream, "Javajaotanreport" + System.currentTimeMillis() + ".txt").queue();
        }
    }

    public static String getVersion() {
        String version = null;
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("version");
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
            version = br.readLine();
        } catch (IOException ignored) {
        }
        return version;
    }

    public static ChatManager getChatManager() {
        return chatManager;
    }
}
