package com.crawler.parser.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class WebPageDTO {

    private String url;
    private String title;
    private String text;
    private Map<String, String> metaData;
    private LocalDateTime crawlDate;

    public WebPageDTO() {
        this.crawlDate = LocalDateTime.now();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

    public LocalDateTime getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(LocalDateTime crawlDate) {
        this.crawlDate = crawlDate;
    }
}
