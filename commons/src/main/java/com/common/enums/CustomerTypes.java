package com.common.enums;

import lombok.Getter;

@Getter
public enum CustomerTypes {
    RETAIL("retail"),
    CORPORATE("corporate"),
    INVESTMENT("investment");

    private final String type;

    CustomerTypes(String type) {
        this.type = type;
    }
}