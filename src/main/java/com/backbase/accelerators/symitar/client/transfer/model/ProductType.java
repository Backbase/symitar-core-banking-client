package com.backbase.accelerators.symitar.client.transfer.model;

import lombok.Getter;

@Getter
public enum ProductType {

    SHARE((short) 0),
    LOAN((short) 1);

    private final short value;

    ProductType(short value) {
        this.value = value;
    }
}
