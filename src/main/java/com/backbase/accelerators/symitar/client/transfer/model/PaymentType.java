package com.backbase.accelerators.symitar.client.transfer.model;

import lombok.Getter;

@Getter
public enum PaymentType {

    STANDARD_PAYMENT((short) 0),
    ADDITIONAL_PAYMENT((short) 1);

    private final short value;

    PaymentType(short value) {
        this.value = value;
    }
}
