package com.backbase.accelerators.symitar.client.transfer.model;

import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanTransfer;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanTransfer;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransfer;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetTransferListResponse {

    List<ShareTransfer> shareTransfers;
    List<LoanTransfer> loanTransfers;
    List<ExternalLoanTransfer> externalLoanTransfers;
}
