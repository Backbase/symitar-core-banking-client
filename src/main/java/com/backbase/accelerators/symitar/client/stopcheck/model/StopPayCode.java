package com.backbase.accelerators.symitar.client.stopcheck.model;

import lombok.Getter;

@Getter
public enum StopPayCode {

    UNKNOWN_OR_NOT_PROVIDED((short) 0),
    LOST((short) 1),
    STOLEN((short) 2),
    DESTROYED((short) 3),
    NOT_ENDORSED((short) 4),
    CERTIFIED((short) 5),
    DISPUTED((short) 6),
    RETURNED_MERCHANDISE((short) 7),
    STOPPED_OR_CANCELED_SERVICE((short) 8),
    OTHER((short) 99);

    private final short value;

    StopPayCode(short value) {
        this.value = value;
    }
}
