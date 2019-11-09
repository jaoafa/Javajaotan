package com.jaoafa.Javajaotan.Command;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Main;
import com.jaoafa.Javajaotan.Lib.ClassFinder;
import com.jaoafa.Javajaotan.Lib.EmbedField;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Help implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		// help command
		if (args.length == 0) {
			// cmd list
			List<EmbedField> cmdList = new ArrayList<>();
			try {
				ClassFinder classFinder = new ClassFinder();
				for (Class<?> clazz : classFinder.findClasses("com.jaoafa.Javajaotan.Command")) {
					if (!clazz.getName().startsWith("com.jaoafa.Javajaotan.Command.Cmd_")) {
						continue;
					}
					if (clazz.getName().contains("$")) {
						continue;
					}
					String commandName = clazz.getName().substring("com.jaoafa.Javajaotan.Command.Cmd_".length());

					Constructor<?> construct = clazz.getConstructor();
					CommandPremise cmd = (CommandPremise) construct.newInstance();
					if (cmd.isjMSOnly() && guild.getIdLong() != 597378876556967936L) {
						continue;
					}
					String description = cmd.getDescription();
					if (description == null) {
						description = "null";
					}
					EmbedField field = new EmbedField(commandName.toLowerCase(), description);
					cmdList.add(field);
				}
			} catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
				channel.sendMessage("処理に失敗しました。時間を置いてもう一度お試しください。\n"
						+ "**Message**: `" + e.getMessage() + "`").queue();
				Main.ExceptionReporter(channel, e);
				return;
			}
			// todo: /help 2など対応、文字数計算・自動的に切ってページネーションする
			// 10コマンドづつページ分割
			final int nowpage = 0; // 1ページ = 0
			final int allcmdcount = cmdList.size();
			int allpage = allcmdcount / 10;
			if (allcmdcount % 10 != 0)
				allpage++;
			final int pagestart = nowpage * 10;
			final int pageend = nowpage * 10 + 10;

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("jaotan Commands (" + (nowpage + 1) + " / " + allpage + ")");
			builder.setColor(Color.YELLOW);
			if (nowpage < allpage) {
				builder.setFooter("View more command by typing /help <Page>.");
			}
			for (int i = pagestart; i < pageend; i++) {
				if (i < 0 || i >= cmdList.size()) {
					break;
				}
				EmbedField field = cmdList.get(i);
				builder.addField("/" + field.getTitle(), field.getContent(), true);
			}
			channel.sendMessage(builder.build()).queue();
			return;
		} else if (args.length == 1 && NumberUtils.isDigits(args[0])) {
			// /help 1,2,3...
			List<EmbedField> cmdList = new ArrayList<>();
			try {
				ClassFinder classFinder = new ClassFinder();
				//Set<Class<?>> classes = Library.getClasses("com.jaoafa.Javajaotan.Command");
				for (Class<?> clazz : classFinder.findClasses("com.jaoafa.Javajaotan.Command")) {
					if (!clazz.getName().startsWith("com.jaoafa.Javajaotan.Command.Cmd_")) {
						continue;
					}
					if (clazz.getName().contains("$")) {
						continue;
					}
					String commandName = clazz.getName().substring("com.jaoafa.Javajaotan.Command.Cmd_".length());

					Constructor<?> construct = clazz.getConstructor();
					CommandPremise cmd = (CommandPremise) construct.newInstance();
					if (cmd.isjMSOnly() && guild.getIdLong() != 597378876556967936L) {
						continue;
					}
					String description = cmd.getDescription();
					if (description == null) {
						description = "null";
					}
					EmbedField field = new EmbedField(commandName.toLowerCase(), description);
					cmdList.add(field);
				}
			} catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
				channel.sendMessage("処理に失敗しました。時間を置いてもう一度お試しください。\n"
						+ "**Message**: `" + e.getMessage() + "`").queue();
				Main.ExceptionReporter(channel, e);
				return;
			}
			// todo: /help 2など対応、文字数計算・自動的に切ってページネーションする
			// 10コマンドづつページ分割
			final int nowpage = Integer.valueOf(args[0]) - 1; // 1ページ = 0
			final int allcmdcount = cmdList.size();
			int allpage = allcmdcount / 10;
			if (allcmdcount % 10 != 0)
				allpage++;
			final int pagestart = nowpage * 10;
			final int pageend = nowpage * 10 + 10;

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("jaotan Commands (" + (nowpage + 1) + " / " + allpage + ")");
			builder.setColor(Color.YELLOW);
			if (nowpage < allpage) {
				builder.setFooter("View more command by typing /help <Page>.");
			}

			for (int i = pagestart; i < pageend; i++) {
				if (i < 0 || i >= cmdList.size()) {
					break;
				}
				EmbedField field = cmdList.get(i);
				builder.addField("/" + field.getTitle(), field.getContent(), true);
			}

			channel.sendMessage(builder.build()).queue();
			return;
		}
		try {
			String className = args[0].substring(0, 1).toUpperCase() + args[0].substring(1).toLowerCase(); // Help
			//channel.sendMessage("com.jaoafa.Javajaotan.Command.Cmd_" + className);

			Class.forName("com.jaoafa.Javajaotan.Command.Cmd_" + className);
			// クラスがない場合これ以降進まない
			Constructor<?> construct = Class.forName("com.jaoafa.Javajaotan.Command.Cmd_" + className)
					.getConstructor();
			CommandPremise cmd = (CommandPremise) construct.newInstance();
			if (cmd.isjMSOnly() && guild.getIdLong() != 597378876556967936L) {
				throw new ClassNotFoundException(); // 存在しないものとして。
			}
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("jaotan Command Help");
			builder.setColor(Color.YELLOW);
			String description = cmd.getDescription();
			if (description == null) {
				description = "null";
			}
			String usage = cmd.getUsage();
			if (usage == null) {
				usage = "null";
			}
			builder.addField("/" + args[0].toLowerCase(),
					"**Description**: `" + description + "`\n" + "**Usage**: `" + usage + "`", false);

			channel.sendMessage(builder.build()).queue();
			return;
		} catch (ClassNotFoundException e) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("jaotan Command Help");
			builder.setColor(Color.YELLOW);
			builder.addField("/" + args[0].toLowerCase(), "Command not found.", false);

			channel.sendMessage(builder.build()).queue();
			return;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			channel.sendMessage("処理に失敗しました。時間を置いてもう一度お試しください。\n"
					+ "**Message**: `" + e.getMessage() + "`").queue();
			Main.ExceptionReporter(channel, e);
		}
	}

	@Override
	public String getDescription() {
		return "コマンドの説明と使用方法を表示します。引数を指定しない場合、コマンドの一覧を表示します。";
	}

	@Override
	public String getUsage() {
		return "/help [Command|Page]";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}
}