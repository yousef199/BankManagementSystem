package com.customer.kafka;

import com.common.enums.TopicNames;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author YQadous
 * Kafka topic configuration
 */
@Configuration
public class KafkaTopicConfiguration {

    public NewTopic newCustomerTopic() {
        return TopicBuilder.
                name(TopicNames.CUSTOMER_NEW.getTopicName())
                .build();
    }

    public NewTopic updateCustomerTopic() {
        return TopicBuilder.
                name(TopicNames.CUSTOMER_UPDATE.getTopicName())
                .build();
    }

    public NewTopic deleteCustomerTopic() {
        return TopicBuilder.
                name(TopicNames.CUSTOMER_DELETE.getTopicName())
                .build();
    }
}
