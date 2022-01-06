package com.backbase.accelerators.symitar.client.transfer.model;

import lombok.Getter;

@Getter
public enum TransferType {

    AUTO_SHARE_TRANSFER((short) 3),
    OFF_CYCLE((short) 9);

    private final short value;

    TransferType(short value) {
        this.value = value;
    }
}
