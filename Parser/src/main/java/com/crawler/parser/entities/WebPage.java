package com.crawler.parser.entities;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Table(name = "web_pages")
@Data
public class WebPage {
    @Id
    private String url;
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String text;
    @ElementCollection
    private Map<String, String> metaData;
    private LocalDateTime crawlDate;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

    public void setCrawlDate(LocalDateTime crawlDate) {
        this.crawlDate = crawlDate;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public LocalDateTime getCrawlDate() {
        return crawlDate;
    }
}
