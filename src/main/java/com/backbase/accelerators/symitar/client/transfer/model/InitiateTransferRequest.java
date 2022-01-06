package com.backbase.accelerators.symitar.client.transfer.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InitiateTransferRequest {

    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private String sourceProductId;
    private String destinationProductId;
    private String recipientId;

    private int externalLoanLocator;

    private ProductType sourceProductType;
    private ProductType destinationProductType;
    private Short day1;
    private Short day2;
    private Short frequency;

    private BigDecimal amount;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private LocalDate nextDate;
}
