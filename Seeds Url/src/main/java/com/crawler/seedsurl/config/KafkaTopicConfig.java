package com.crawler.seedsurl.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic urlTopic() {
        return TopicBuilder.name("crawler-urls")
                .build();
    }
}
