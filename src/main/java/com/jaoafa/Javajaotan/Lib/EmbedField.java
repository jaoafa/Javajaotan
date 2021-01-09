package com.jaoafa.Javajaotan.Lib;

public class EmbedField {
    private final String title;
    private final String content;

    public EmbedField(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
