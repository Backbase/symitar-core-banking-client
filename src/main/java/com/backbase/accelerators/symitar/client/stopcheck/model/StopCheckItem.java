package com.backbase.accelerators.symitar.client.stopcheck.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class StopCheckItem {

    private String productId;
    private String description;
    private String micrAccountNumber;
    private Integer holdLocator;
    private BigDecimal amount;
    private LocalDate dateSubmitted;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private String payeeName;
    private String startingCheckNumber;
    private String endingCheckNumber;
    private Integer stopPayCode;
    private String otherReason;
    private String singleOrRange;
    private String status;
}
