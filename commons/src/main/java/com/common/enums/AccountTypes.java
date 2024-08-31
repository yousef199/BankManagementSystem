package com.common.enums;

import lombok.Getter;

@Getter
public enum AccountTypes {
    SALARY("salary"),
    SAVINGS("savings"),
    INVESTMENT("investment");

    private String type;

    AccountTypes(String type) {
        this.type = type;
    }
}