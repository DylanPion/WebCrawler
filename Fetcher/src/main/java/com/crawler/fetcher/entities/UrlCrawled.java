package com.crawler.fetcher.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "url_crawled")
@Getter
@Setter
public class UrlCrawled {

    public UrlCrawled() {}
    public UrlCrawled(String url) {
        this.url = url;
        this.crawlDate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    private LocalDateTime crawlDate;

}


