package com.backbase.accelerators.symitar.client.poweron.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AchPendingTransactions {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("effectivedate")
    private String effectiveDate;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("expirationdate")
    private String expirationDate;

    @JsonProperty("payeename")
    private String payeeName;

    @JsonProperty("reference1")
    private String reference1;

    @JsonProperty("reference2")
    private String reference2;
}
