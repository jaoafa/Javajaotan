package com.jaoafa.Javajaotan.Lib;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PriconeCharacter {
	private String characterImgUrl;
	private String characterUrl;
	private String characterName;
	private String role;
	private String score_NonDedicated;
	private String score_Dedicated;
	private String score_star6;
	private String rating_quest = null;
	private String rating_runa = null;
	private String rating_arena = null;
	private String rating_clan = null;

	public PriconeCharacter(String characterImgUrl, String characterUrl, String characterName, String role,
			String score_NonDedicated, String score_Dedicated, String score_star6) {
		this.characterImgUrl = characterImgUrl;
		this.characterUrl = characterUrl;
		this.characterName = characterName;
		this.role = role;
		this.score_NonDedicated = score_NonDedicated;
		this.score_Dedicated = score_Dedicated;
		this.score_star6 = score_star6;
		try {
			Document doc_chara = Jsoup.connect(characterUrl).get();
			Element ratingTable = doc_chara.selectFirst("div.puri_hyouka_2 table");
			Elements rating_trs = ratingTable.select("tr");
			Element rating_tr = rating_trs.get(1);
			Elements rating_tds = rating_tr.select("td");

			// クエスト
			rating_quest = ImgUrlToRating(rating_tds.get(0).select("img").attr("src"));
			// ルナの塔
			rating_runa = ImgUrlToRating(rating_tds.get(1).select("img").attr("src"));
			// アリーナ
			rating_arena = ImgUrlToRating(rating_tds.get(2).select("img").attr("src"));
			// クラバト
			rating_clan = ImgUrlToRating(rating_tds.get(3).select("img").attr("src"));
		} catch (IOException e) {
		}
	}

	private String ImgUrlToRating(String imgurl) {
		Pattern p = Pattern.compile("hyouka_([A-Z]+)\\.png");
		Matcher m = p.matcher(imgurl);
		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}

	public String getCharacterImgUrl() {
		return characterImgUrl;
	}

	public String getCharacterUrl() {
		return characterUrl;
	}

	public String getCharacterName() {
		return characterName;
	}

	public String getRole() {
		return role;
	}

	public String getScore_NonDedicated() {
		return score_NonDedicated;
	}

	public String getScore_Dedicated() {
		return score_Dedicated;
	}

	public String getScore_star6() {
		return score_star6;
	}

	public String getQuestRating() {
		return rating_quest;
	}

	public String getRunaRating() {
		return rating_runa;
	}

	public String getArenaRating() {
		return rating_arena;
	}

	public String getClanRating() {
		return rating_clan;
	}
}
