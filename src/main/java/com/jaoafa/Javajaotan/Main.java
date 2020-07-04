package com.jaoafa.Javajaotan;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jaoafa.Javajaotan.ALLChat.ALLChatMainEvent;
import com.jaoafa.Javajaotan.Channel.ChannelMainEvent;
import com.jaoafa.Javajaotan.Command.MessageMainEvent;
import com.jaoafa.Javajaotan.Event.Event_ReactionAddEvent;
import com.jaoafa.Javajaotan.Event.Event_ServerBanned;
import com.jaoafa.Javajaotan.Event.Event_ServerJoin;
import com.jaoafa.Javajaotan.Event.Event_ServerLeave;
import com.jaoafa.Javajaotan.Event.Event_TodoCheck;
import com.jaoafa.Javajaotan.Event.Event_TomachiEmojis;
import com.jaoafa.Javajaotan.Lib.ChatManager;
import com.jaoafa.Javajaotan.Lib.Library;
import com.jaoafa.Javajaotan.Lib.MySQLDBManager;
import com.jaoafa.Javajaotan.Lib.PriconeCharacter;
import com.jaoafa.Javajaotan.Task.Task_MeetingVote;
import com.jaoafa.Javajaotan.Task.Task_VerifiedCheck;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;

public class Main {
	public static TextChannel ReportChannel = null;
	private static JDA jda = null;
	public static MySQLDBManager MySQLDBManager = null;
	public static String translateGAS = null;
	public static List<PriconeCharacter> pricone_Characters = new ArrayList<>();
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
			try {
				props.store(new FileOutputStream("conf.properties"), "Comments");
				System.out.println("Please Config Token!");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
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
		chatManager = new ChatManager(nobyAPIKey, userlocalAPIKey, A3RTAPIKey);

		// 分けてイベント自動登録できるように？
		// 全部JDA移行
		try {
			JDABuilder jdabuilder = new JDABuilder(AccountType.BOT)
					.setAutoReconnect(true)
					.setBulkDeleteSplittingEnabled(false)
					.setToken(token)
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
						() -> {
							System.out.println("Exit");
						}));

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new Task_VerifiedCheck(), 10000, 60000); // 1分
		timer.scheduleAtFixedRate(new Task_MeetingVote(), 10000, 600000); // 10分

		/*
		JavajaotanWatcher JavajaotanWatcher = new JavajaotanWatcher();
		Timer timer = new Timer();
		timer.schedule(JavajaotanWatcher, 60000);
		*/
	}

	public static void setJDA(JDA jda) {
		Main.jda = jda;
	}

	public static JDA getJDA() {
		return jda;
	}

	public static void DiscordExceptionError(@NotNull Class<?> clazz, @Nullable MessageChannel channel,
			@NotNull Throwable exception) {
		if (channel == null && Main.ReportChannel != null) {
			channel = Main.ReportChannel;
		} else if (channel == null) {
			System.out.println("DiscordExceptionError: channel == null and Javajaotan.ReportChannel == null.");
			System.out.println("DiscordExceptionError did not work properly!");
			return;
		}
		if (clazz == null) {
			throw new NullPointerException("Class<?> clazz is null!");
		}
		if (exception == null) {
			throw new NullPointerException("DiscordException exception is null!");
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		pw.flush();
		try {
			InputStream is = new ByteArrayInputStream(sw.toString().getBytes("utf-8"));
			channel.sendMessage(":pencil:おっと！Javajaotanでなにか問題が発生したようです！ <@221991565567066112>\n**ErrorMsg**: `"
					+ exception.getMessage()
					+ "`\n**Class**: `" + clazz.getName() + " (" + exception.getClass().getName() + ")`").queue();
			channel.sendFile(is, "stacktrace.txt").queue();
		} catch (UnsupportedEncodingException ex) {
			channel.sendMessage(":pencil:<@221991565567066112> おっと！メッセージ送信時に問題が発生したみたいです！\n**ErrorMsg**: `"
					+ exception.getMessage() + "`\n**Class**: `" + clazz.getName() + " ("
					+ exception.getClass().getName() + ")`\nUnsupportedEncodingException: `" + ex.getMessage() + "`");
		}
	}

	public static void ExceptionReporter(@Nullable MessageChannel channel, @NotNull Throwable exception) {
		if (channel != null) {
			channel.sendMessage(
					":pencil:おっと！Javajaotanでなにか問題が発生したようです！ <@221991565567066112>\n**Throwable Class**: `"
							+ exception.getClass().getName() + "`")
					.queue();
		}
		if (Main.ReportChannel == null) {
			System.out.println("ExceptionReporter: Javajaotan.ReportChannel == null.");
			System.out.println("ExceptionReporter did not work properly!");
			return;
		}
		if (exception == null) {
			throw new NullPointerException("Throwable exception is null!");
		}
		exception.printStackTrace();

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);

		try {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("javajaotan Error Reporter");
			builder.setColor(Color.RED);
			builder.addField("StackTrace", "```" + sw.toString() + "```", false);
			builder.addField("Message", "```" + exception.getMessage() + "```", false);
			builder.addField("Cause", "```" + exception.getCause() + "```", false);
			builder.setTimestamp(Instant.now());
			channel.sendMessage(builder.build()).queue();
		} catch (Exception e) {
			try {
				String text = "javajaotan Error Reporter (" + Library.sdfFormat(new Date()) + ")\n"
						+ "---------- StackTrace ----------\n"
						+ sw.toString() + "\n"
						+ "---------- Message ----------\n"
						+ exception.getMessage() + "\n"
						+ "---------- Cause ----------\n"
						+ exception.getCause();
				InputStream stream = new ByteArrayInputStream(
						text.getBytes("utf-8"));
				Main.ReportChannel.sendFile(stream, "Javajaotanreport" + System.currentTimeMillis() + ".txt").queue();
			} catch (UnsupportedEncodingException ex) {
				return;
			}
		}
	}

	public static String getVersion() {
		String version = null;
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("version");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			version = br.readLine();
		} catch (IOException e) {
		}
		return version;
	}

	public static ChatManager getChatManager() {
		return chatManager;
	}
}
