package com.crawler.fetcher.repositories;

import com.crawler.fetcher.entities.UrlCrawled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlCrawledRepository extends JpaRepository<UrlCrawled, Long> {
    Optional<UrlCrawled> findByUrl(String url);
}
