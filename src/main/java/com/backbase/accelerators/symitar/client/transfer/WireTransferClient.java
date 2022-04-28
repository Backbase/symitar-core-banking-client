package com.backbase.accelerators.symitar.client.transfer;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.transactions.TransactionsService;
import com.symitar.generated.symxchange.transactions.dto.DonorIdType;
import com.symitar.generated.symxchange.transactions.dto.GLCodes;
import com.symitar.generated.symxchange.transactions.dto.TransactionsBaseResponse;
import com.symitar.generated.symxchange.transactions.dto.WithdrawFeeRequest;
import com.symitar.generated.symxchange.wire.CreateWireRequest;
import com.symitar.generated.symxchange.wire.WireCreateResponse;
import com.symitar.generated.symxchange.wire.WireService;
import com.symitar.generated.symxchange.wire.dto.create.WireCreatableFields;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class WireTransferClient {

    private final SymitarRequestSettings symitarRequestSettings;
    private final WireService wireService;
    private final TransactionsService transactionsService;

    public WireTransferClient(
        SymitarRequestSettings symitarRequestSettings,
        WireService wireService,
        TransactionsService transactionsService) {

        Objects.requireNonNull(
            wireService,
            "WireTransferClient cannot be initialized because WireService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "WireTransferClient cannot be initialized because SymxRequestSettings is null.");

        Objects.requireNonNull(
            transactionsService,
            "WireTransferClient cannot be initialized because TransactionsService is null.");

        this.symitarRequestSettings = symitarRequestSettings;
        this.wireService = wireService;
        this.transactionsService = transactionsService;
    }

    /**
     * Creates a domestic wire transfer.
     *
     * @param wireCreatableFields contains the transfer details such as the amount, source & destination of the transfer
     * @return a WireCreateResponse containing the wire sequence number
     */
    public WireCreateResponse createDomesticWireTransfer(WireCreatableFields wireCreatableFields) {
        CreateWireRequest createWireRequest = new CreateWireRequest();
        createWireRequest.setMessageId(symitarRequestSettings.getMessageId());
        createWireRequest.setCredentials(symitarRequestSettings.getAdminCredentialsChoice());
        createWireRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        createWireRequest.setWireCreatableFields(wireCreatableFields);

        log.debug("Invoking createWire() with request: {}", SymitarUtils.toXmlString(createWireRequest));
        return wireService.createWire(createWireRequest);
    }

    /**
     * Performs a withdrawal of a wire transfer fee.
     *
     * @param accountNumber the member account number
     * @param donorId the unique identifier of the share or loan from which the fee with be withdrawn from
     * @return
     */
    public TransactionsBaseResponse doWireFeeTransfer(String accountNumber, String donorId, DonorIdType donorIdType) {

        WithdrawFeeRequest withdrawFeeRequest = new WithdrawFeeRequest();
        withdrawFeeRequest.setMessageId(symitarRequestSettings.getMessageId());
        withdrawFeeRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        withdrawFeeRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        withdrawFeeRequest.setAccountNumber(SymitarUtils.leftPadAccountNumber(accountNumber));
        withdrawFeeRequest.setDonorId(donorId);
        withdrawFeeRequest.setDonorType(donorIdType);
        withdrawFeeRequest.setEffectiveDate(SymitarUtils.convertToXmlGregorianCalendar(LocalDate.now()));

        Optional.ofNullable(symitarRequestSettings.getWireTransferSettings()).ifPresent(
            wireTransferSettings -> {
                GLCodes glCodes = new GLCodes();
                glCodes.setClearingCode(wireTransferSettings.getGeneralLedgerClearingCode());

                withdrawFeeRequest.setTotalAmount(wireTransferSettings.getWithdrawalFeeAmount());
                withdrawFeeRequest.setFeeCode(wireTransferSettings.getWithdrawalFeeCode());
                withdrawFeeRequest.setComment(wireTransferSettings.getWithdrawalFeeReasonText());
                withdrawFeeRequest.setSourceCode(wireTransferSettings.getSourceCode());
                withdrawFeeRequest.setGLCodes(glCodes);
            }
        );

        log.debug("Invoking withdrawFee() with request: {}", SymitarUtils.toXmlString(withdrawFeeRequest));
        return transactionsService.withdrawFee(withdrawFeeRequest);
    }
}
