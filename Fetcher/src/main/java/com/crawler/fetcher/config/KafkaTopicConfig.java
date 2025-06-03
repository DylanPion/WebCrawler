package com.crawler.fetcher.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic urlTopic() {
        // TODO : topic de test crawler-urls
        return TopicBuilder.name("crawlers-url")
                .build();
    }
}
