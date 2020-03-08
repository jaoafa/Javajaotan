package com.jaoafa.Javajaotan.Command;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jaoafa.Javajaotan.CommandPremise;
import com.jaoafa.Javajaotan.Lib.PriconeCharacter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Cmd_Pricone implements CommandPremise {
	@Override
	public void onCommand(JDA jda, Guild guild, MessageChannel channel, Member member,
			Message message, String[] args) {
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("rating")) {
				if (characters.size() == 0) {
					try {
						crawlGameWith();
					} catch (IOException | ClassNotFoundException e) {
						channel.sendMessage(member.getAsMention() + ", キャラクター情報の取得に失敗しました。").queue();
						return;
					}
				}
				String[] characterSearchName = Arrays.copyOfRange(args, 1, args.length);
				PriconeCharacter selectCharacter = null;
				for (PriconeCharacter character : characters) {
					boolean isMatch = false;
					for (String searchText : characterSearchName) {
						if (character.getCharacterName().contains(searchText)) {
							isMatch = true;
						}
					}
					if (isMatch) {
						selectCharacter = character;
						break;
					}
				}
				if (selectCharacter == null) {
					channel.sendMessage(member.getAsMention() + ", 指定されたキャラ名にマッチするキャラクターは見つかりませんでした。").queue();
					return;
				}
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("プリコネR キャラクターレーティング (GameWith)");
				embed.setColor(Color.GREEN);
				embed.addField("キャラ名", selectCharacter.getCharacterName(), false);
				embed.addField("役割", selectCharacter.getRole(), false);
				embed.addField("点数(専用無)", selectCharacter.getScore_NonDedicated() + "点", true);
				embed.addField("点数(専用有)", selectCharacter.getScore_Dedicated() + "点", true);
				embed.addField("点数(星6)", selectCharacter.getScore_star6() + "点", true);
				embed.addField("評価(クエスト)", selectCharacter.getQuestRating(), true);
				embed.addField("評価(アリーナ)", selectCharacter.getArenaRating(), true);
				embed.addField("評価(クランバトル)", selectCharacter.getClanRating(), true);
				embed.setImage(selectCharacter.getCharacterImgUrl());
				embed.setAuthor("プリコネ攻略(全キャラ評価一覧) - GameWith", "https://gamewith.jp/pricone-re/article/show/92923");
				embed.setTimestamp(Instant.now());
				channel.sendMessage(embed.build()).queue();
				return;
			}
		}
		channel.sendMessage(member.getAsMention() + ", `" + getUsage() + "`").queue();
	}

	@Override
	public String getDescription() {
		return "やばいですね☆";
	}

	@Override
	public String getUsage() {
		return "/pricone <rating> <CharacterName...>: キャラクターのレーティングを調べます。キャラ名はスペースで区切ることでAND検索ができます。";
	}

	@Override
	public boolean isjMSOnly() {
		return false;
	}

	static List<PriconeCharacter> characters = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void crawlGameWith() throws IOException, ClassNotFoundException {
		File file = new File("data.dat");
		System.out.println(file.getAbsolutePath());
		if (file.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			characters = (List<PriconeCharacter>) ois.readObject();
			ois.close();
			return;
		}
		Document doc = Jsoup.connect("https://gamewith.jp/pricone-re/article/show/92923").get();
		Element table = doc.selectFirst(".puri_chara table");
		Elements trs = table.select("tr");
		for (Element tr : trs) {
			Elements tds = tr.select("td");
			if (tds.size() == 0)
				continue;
			// キャラ画像URL
			String characterImgUrl = tds.get(0).select("a img").attr("src");
			// キャラページURL
			String characterUrl = tds.get(0).select("a").attr("href");
			// キャラ名
			String characterName = tds.get(0).select("a").text();
			// 役割
			String role = tds.get(1).text();
			// 点数 (専用なし)
			String score_NonDedicated = tds.get(2).text();
			// 点数 (専用あり)
			String score_Dedicated = tds.get(3).text();
			// 点数 (星6)
			String score_star6 = tds.get(4).text();

			PriconeCharacter character = new PriconeCharacter(characterImgUrl, characterUrl, characterName, role,
					score_NonDedicated, score_Dedicated, score_star6);
			characters.add(character);
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(characters);
		oos.close();
	}
}
