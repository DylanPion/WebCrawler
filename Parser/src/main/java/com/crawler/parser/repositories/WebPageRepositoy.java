package com.crawler.parser.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crawler.parser.entities.WebPage;

public interface WebPageRepositoy extends JpaRepository<WebPage, String> {

}
