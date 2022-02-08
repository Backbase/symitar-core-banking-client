package com.backbase.accelerators.symitar.client.transfer.model;

import lombok.Getter;

@Getter
public enum PaymentFrequency {

    DEMAND((short) 0),
    ANNUAL((short) 1),
    SEMI_ANNUAL((short) 2),
    QUARTERLY((short) 3),
    MONTHLY((short) 4),
    SEMI_MONTHLY((short) 5),
    BI_WEEKLY((short) 8),
    WEEKLY((short) 9),
    BI_MONTHLY((short) 13),
    YEARLY((short) 1),
    DAILY((short) 6),
    ONCE((short) 0);

    private final short value;

    PaymentFrequency(short value) {
        this.value = value;
    }
}
