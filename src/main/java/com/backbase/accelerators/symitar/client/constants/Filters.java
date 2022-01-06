package com.backbase.accelerators.symitar.client.constants;

public interface Filters {

    String SHARE_FILTER = "CloseDate < {date: '1900-01-01'} AND ChargeOffDate < {date: '1900-01-01'}";
    String LOAN_FILTER = "CloseDate < {date: '1900-01-01'} AND ChargeOffDate < {date: '1900-01-01'}";
    String EXTERNAL_LOAN_FILTER = "Type = 2 AND Status = 0";

    String STOP_CHECK_SHARE_CODE_FILTER = "ShareCode = 1";
    String STOP_CHECK_LOAN_CODE_FILTER = "LoanCode = 2";
    String STOP_CHECK_SHARE_HOLD_FILTER = "(Type = 3) AND ExpirationDate > {date:'1900-01-01'}";
}
