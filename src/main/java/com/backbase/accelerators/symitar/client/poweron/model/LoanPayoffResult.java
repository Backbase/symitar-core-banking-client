package com.backbase.accelerators.symitar.client.poweron.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanPayoffResult {

    private boolean error;
    private String errorMessage;
    private String interestRate;
    private String interestType;
    private String loan;
    private String nextDueDate;
    private String payoffDate;
    private String prePayPenaltyExpirationDate;
    private BigDecimal lateChargeYtd;
    private BigDecimal lateChargeLastYear;
    private BigDecimal amountPastDue;
    private BigDecimal balanceXfrPrincipal;
    private BigDecimal billedFeePayoff;
    private BigDecimal cashAdvancePrincipal;
    private BigDecimal countPastDue;
    private BigDecimal cutoffBalanceXfrFinChrg;
    private BigDecimal cutoffBalXfrInterest;
    private BigDecimal cutoffCashAdvanceInterest;
    private BigDecimal cutoffCashTransInterest;
    private BigDecimal cutoffPurchaseInterest;
    private BigDecimal electiveCoverage;
    private BigDecimal feePrincipal;
    private BigDecimal interestDue;
    private BigDecimal lateCharge;
    private BigDecimal purchasePrincipal;
    private BigDecimal salesTaxPayoff;
    private BigDecimal terminateFeePayoff;
    private BigDecimal unpaidBalXfrInterest;
    private BigDecimal unpaidCashAdvanceInterest;
    private BigDecimal unpaidPurchaseInterest;
    private BigDecimal payoffAmount;
    private BigDecimal perDiem;
    private BigDecimal prePaymentPenaltyFee;
    private BigDecimal principal;
}
