package com.backbase.accelerators.symitar.client.exception;

public class SymitarCoreClientException extends RuntimeException {

    public SymitarCoreClientException(String message) {
        super(message);
    }

    public SymitarCoreClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
