package com.common.enums;

import lombok.Getter;

@Getter
public enum CustomerStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String status;

    CustomerStatus(String status) {
        this.status = status;
    }

}