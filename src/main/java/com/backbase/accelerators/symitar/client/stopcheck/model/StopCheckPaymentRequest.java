package com.backbase.accelerators.symitar.client.stopcheck.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StopCheckPaymentRequest {

    private BigDecimal amount;
    private String accountNumber;
    private String productId;
    private String feeAccountNumber;
    private String feeProductId;
    private String otherReason;
    private String payeeName;
    private String startingCheckNumber;
    private String endingCheckNumber;
    private Integer feeCode;
    private Integer type;
    private StopPayCode stopPayCode;
    private LocalDate effectiveDate;
}
