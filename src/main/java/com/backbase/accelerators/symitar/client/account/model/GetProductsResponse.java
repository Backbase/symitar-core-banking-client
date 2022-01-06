package com.backbase.accelerators.symitar.client.account.model;

import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoan;
import com.symitar.generated.symxchange.account.dto.retrieve.Loan;
import com.symitar.generated.symxchange.account.dto.retrieve.Share;
import lombok.Data;

import java.util.List;

@Data
public class GetProductsResponse {

    private Short accountType;
    private List<Share> shares;
    private List<Loan> loans;
    private List<ExternalLoan> externalLoans;
}
