package com.common.enums;

import lombok.Getter;

@Getter
public enum AccountStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    CLOSED("closed");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }
}