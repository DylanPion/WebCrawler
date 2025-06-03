package com.crawler.fetcher.repositories;

import com.crawler.fetcher.entities.UrlCrawled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlCrawledRepository extends JpaRepository<UrlCrawled, Long> {
    /**
     * Finds a URL in the database by its URL string
     * 
     * @param url The URL to search for
     * @return An optional containing the URL if found, or empty if not found
     */
    Optional<UrlCrawled> findByUrl(String url);
}
