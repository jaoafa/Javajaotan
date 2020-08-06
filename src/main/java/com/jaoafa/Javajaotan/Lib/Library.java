package com.jaoafa.Javajaotan.Lib;

import com.google.common.base.Optional;
import com.jaoafa.Javajaotan.Main;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class Library {
    /**
     * 他Discordサーバのウィジェットから、そのサーバに指定した利用者がいるかを判定します
     *
     * @param guild_id 他DiscordサーバのGuildID
     * @param user_id  判定する利用者のID
     * @return 居ればtrue (オンラインのみ)
     */
    public static Boolean checkOtherServerMember(String guild_id, String user_id) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "DiscordBot (https://jaoafacom, v0.1)");
        JSONObject obj = getHttpsJson("https://discord.com/api/guilds/" + guild_id + "/widget.json", headers);
        if (obj.has("code") && obj.getInt("code") == 50004) {
            // disabled
            return false;
        }
        JSONArray members = obj.getJSONArray("members");
        for (Object o : members) {
            if (!(o instanceof JSONObject)) {
                continue;
            }
            JSONObject one = (JSONObject) o;
            if (user_id.equals(one.getString("id"))) {
                return true;
            }
        }
        return false;
    }

    public static JSONObject getHttpsJson(String address, Map<String, String> headers) {
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(address);

            HttpsURLConnection connect = (HttpsURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connect.setRequestProperty(header.getKey(), header.getValue());
                }
            }

            connect.connect();

            if (connect.getResponseCode() != HttpURLConnection.HTTP_OK) {
                InputStream in = connect.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                in.close();
                connect.disconnect();

                System.out.println("ConnectWARN: " + connect.getResponseMessage());
                return null;
            }

            InputStream in = connect.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            in.close();
            connect.disconnect();
            return new JSONObject(builder.toString());
        } catch (Exception e) {
            Main.ExceptionReporter(null, e);
            return null;
        }
    }

    public static String getCurrentpath() {
        String cp = System.getProperty("java.class.path");
        String fs = System.getProperty("file.separator");
        String acp = (new File(cp)).getAbsolutePath();
        int p, q;
        for (p = 0; (q = acp.indexOf(fs, p)) >= 0; p = q + 1) ;
        return acp.substring(0, p);
    }

    public static boolean isAllowRole(Member member, Role[] roles) {
        for (Role role : roles) {
            if (hasRole(member, role)) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static String implode(CharSequence delimiter, CharSequence... elements) {
        return String.join(delimiter, elements);
    }

    /**
     * ホスト名を返す
     *
     * @return ホスト名。取得できなければnullを返却
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            Main.ExceptionReporter(null, e);
            return null;
        }
    }

    public static boolean isInt(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(String s) {
        try {
            Long.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * AdminもしくはModeratorのユーザであるかを判定します。
     *
     * @param guild  対象のGuild
     * @param member 判定するユーザ
     * @return Admin, Moderatorであればtrue
     */
    public static boolean hasAdminModeratorRole(Guild guild, Member member) {
        Role AdminRole;
        Role ModeratorRole;
        if (guild.getIdLong() == 189377932429492224L) {
            AdminRole = guild.getRoleById(189381504059572224L);
            ModeratorRole = guild.getRoleById(281699181410910230L);
        } else if (guild.getIdLong() == 597378876556967936L) {
            AdminRole = guild.getRoleById(597405109290532864L);
            ModeratorRole = guild.getRoleById(597405110683041793L);
        } else {
            return false;
        }
        return hasRole(member, AdminRole) || hasRole(member, ModeratorRole);
    }

    public static boolean hasRole(Member member, Role role) {
        if (member == null || role == null)
            return false;
        return member
                .getRoles()
                .stream().anyMatch(_role -> _role.getIdLong() == role.getIdLong());
    }

    public static String sdfFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date);
    }

	/*public static boolean isNewjMSDiscordServer(IGuild guild) {
		return guild.getLongID() == 597378876556967936L;
	}*/

    public static String GoogleTranslateWeb(String text, String from, String to) {
        try {
            String encodeText = URLEncoder.encode(text, "UTF-8");
            String url = "http://translate.google.com/translate_a/t?client=z&sl=" + from + "&tl=" + to + "&text="
                    + encodeText;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).header("User-Agent", "hoge").build();
            Response response = client.newCall(request).execute();
            if (response.code() == 429) {
                return null;
            }
            String res = response.body().string();
            response.close();
            System.out.println("GoogleTranslateWeb Result: " + res);
            if (res.charAt(0) == '[' && res.endsWith("]")) {
                // json | auto?
                JSONArray json = new JSONArray(res);
                if (json.length() == 2) {
                    // from = json.getString(1);
                    return json.getString(0);
                }
                return res;
            } else if (res.charAt(0) == '"' && res.endsWith("\"")) {
                return res.substring(1, res.length() - 1);
            } else {
                return res;
            }
            // auto ["こんにちは","en"]
            // other "こんにちは"
        } catch (IOException | JSONException e) {
            Main.ExceptionReporter(null, e);
            return null;
        }
    }

    public static String getLang(String text) throws IOException {
        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

        TextObjectFactory textObjectFactory;
        if (text.contains(" ") || text.contains("　")) {
            textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
        } else {
            textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText();
        }

        TextObject textObject = textObjectFactory.forText(text);
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        List<DetectedLanguage> langs = languageDetector.getProbabilities(textObject);
        if (lang.isPresent()) {
            return lang.get().getLanguage();
        } else if (!langs.isEmpty()) {
            return langs.get(0).getLocale().getLanguage();
        }
        return null;
    }

    /**
     * 日本語・英語・簡体字中国語・フランス語・ドイツ語・スペイン語・タイ語
     *
     * @param text
     * @return
     * @throws IOException
     */
    public static String getRefineLang(String text) throws IOException {
        List<LdLocale> languages = new ArrayList<>();
        languages.add(LdLocale.fromString("ja"));
        languages.add(LdLocale.fromString("en"));
        languages.add(LdLocale.fromString("fr"));
        languages.add(LdLocale.fromString("de"));
        languages.add(LdLocale.fromString("es"));
        languages.add(LdLocale.fromString("th"));
        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readBuiltIn(languages);
        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

        TextObjectFactory textObjectFactory;
        if (text.contains(" ") || text.contains("　")) {
            textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
        } else {
            textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText();
        }

        TextObject textObject = textObjectFactory.forText(text);
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        List<DetectedLanguage> langs = languageDetector.getProbabilities(textObject);
        if (lang.isPresent()) {
            return lang.get().getLanguage();
        } else if (!langs.isEmpty()) {
            return langs.get(0).getLocale().getLanguage();
        }
        return null;
    }

    public static String GoogleTranslateGAS(String text, String from, String to) {
        if (Main.translateGAS == null) {
            return null;
        }
        try {
            String url = Main.translateGAS;
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("text", text);
            formBuilder.add("before", from);
            formBuilder.add("after", to);
            RequestBody body = formBuilder.build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200 && response.code() != 302) {
                return null;
            }
            JSONObject json = new JSONObject(response.body().string());
            response.close();
            String res = json.getJSONObject("params").getJSONObject("parameter").getString("result");
            if (res.equalsIgnoreCase("undefined")) {
                return null;
            }
            return res;
        } catch (UnsupportedEncodingException e) {
            Main.ExceptionReporter(null, e);
            return null;
        } catch (IOException e) {
            Main.ExceptionReporter(null, e);
            return null;
        }
    }

    public static double calcBlockNumber(List<Integer> X, List<Integer> Z) {
        double size = 0; // 面積
        double side = 0; // 辺の長さ
        double blocks = 0; // ブロック数

        double x1 = 0; // 1点目のX座標値
        double x2 = 0; // 2点目のX座標値
        double z1 = 0; // 1点目のZ座標値
        double z2 = 0; // 2点目のZ座標値

        /* 図形の面積を計算 */
        for (int i = 0; i < X.size(); i++) {
            if ((i + 1) >= X.size()) {
                x1 = X.get(i);
                x2 = X.get(0);
                z1 = Z.get(i);
                z2 = Z.get(0);
            } else {
                x1 = X.get(i);
                x2 = X.get(i + 1);
                z1 = Z.get(i);
                z2 = Z.get(i + 1);
            }
            // 外積を計算して加算
            size += (x1 * z2) - (x2 * z1);
        }
        size = size / 2;
        size = Math.abs(size);

        for (int i = 0; i < X.size(); i++) {
            if ((i + 1) >= X.size()) {
                side = side
                        + Math.abs(X.get(i) - X.get(0))
                        + Math.abs(Z.get(i) - Z.get(0));
            } else {
                side = side
                        + Math.abs(X.get(i) - X.get(i + 1))
                        + Math.abs(Z.get(i) - Z.get(i + 1));
            }
        }

        /* ブロック数を計算 */
        if (size > 0) {
            // ブロック数 = 面積 + (辺の長さ / 2) + 1
            blocks = size + (side / 2) + 1;
        }
        return blocks;
    }

    public static boolean checkBlocks(List<Integer> X, List<Integer> Z) {
        int oldx = X.get(0);
        int oldz = Z.get(0);
        String changed = null; // 変化したのがXかZか。最初はnull、XまたはZを代入
        for (int i = 1; i <= X.size(); i++) {
            int x;
            int z;
            if (i == X.size()) {
                x = X.get(0);
                z = Z.get(0);
            } else {
                x = X.get(i);
                z = Z.get(i);
            }
            if (changed == null) {
                // 最初だけ動作
                if (oldx != x && oldz == z) {
                    // Xが変わってZは変わっていない
                    oldx = x;
                    changed = "X";
                } else if (oldx == x && oldz != z) {
                    // Xが変わっていなくてZは変わっている
                    oldz = z;
                    changed = "Z";
                } else {
                    // XとZ両方変わっているもしくは両方変わっていない
                    return false;
                }
            } else {
                // 最初以外動作
                if (changed.equals("Z") && oldx != x && oldz == z) {
                    // 前回Zが変わっていて、Xが変わってZは変わっていない
                    oldx = x;
                    changed = "X";
                } else if (changed.equals("X") && oldx == x && oldz != z) {
                    // 前回Xが変わっていて、Xが変わっていなくてZは変わっている
                    oldz = z;
                    changed = "Z";
                } else {
                    // XとZ両方変わっているもしくは両方変わっていない
                    // またはX,Zが連続して変わった
                    return false;
                }
            }
        }
        return true;
    }
}
