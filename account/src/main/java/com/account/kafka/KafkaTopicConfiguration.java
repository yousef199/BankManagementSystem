package com.account.kafka;

import com.common.enums.TopicNames;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    public NewTopic newAccountTopic() {
        return TopicBuilder.
                name(TopicNames.ACCOUNT_NEW.getTopicName())
                .build();
    }

    public NewTopic updateAccountTopic() {
        return TopicBuilder.
                name(TopicNames.ACCOUNT_UPDATE.getTopicName())
                .build();
    }

    public NewTopic deleteAccountTopic() {
        return TopicBuilder.
                name(TopicNames.ACCOUNT_DELETE.getTopicName())
                .build();
    }
}
