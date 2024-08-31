package com.common.enums;

public enum TopicNames {
    CUSTOMER_NEW("customer.new"),
    CUSTOMER_UPDATE("customer.update"),
    CUSTOMER_DELETE("customer.delete"),
    ACCOUNT_NEW("account.new"),
    ACCOUNT_UPDATE("account.update"),
    ACCOUNT_DELETE("account.delete"),
    ACCOUNT_TRANSFER("account.transfer");

    private final String topicName;

    TopicNames(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}