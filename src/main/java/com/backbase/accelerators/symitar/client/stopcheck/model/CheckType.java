package com.backbase.accelerators.symitar.client.stopcheck.model;

import lombok.Getter;

@Getter
public enum CheckType {

    SINGLE("S"),
    RANGE("R");

    private final String value;

    CheckType(String value) {
        this.value = value;
    }
}
