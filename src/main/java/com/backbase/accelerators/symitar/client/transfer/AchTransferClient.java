package com.backbase.accelerators.symitar.client.transfer;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.account.AccountService;
import com.symitar.generated.symxchange.account.CreateEftRequest;
import com.symitar.generated.symxchange.account.EftCreateResponse;
import com.symitar.generated.symxchange.account.EftUpdateByIDResponse;
import com.symitar.generated.symxchange.account.UpdateEftByIDRequest;
import com.symitar.generated.symxchange.account.dto.create.EftCreatableFields;
import com.symitar.generated.symxchange.account.dto.update.EftUpdateableFields;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class AchTransferClient {

    private final SymitarRequestSettings symitarRequestSettings;
    private final AccountService accountService;

    public AchTransferClient(
        AccountService accountService,
        SymitarRequestSettings symitarRequestSettings) {

        Objects.requireNonNull(
            accountService,
            "AchTransferClient cannot be initialized because AccountService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "AchTransferClient cannot be initialized because SymitarRequestSettings is null.");

        this.accountService = accountService;
        this.symitarRequestSettings = symitarRequestSettings;
    }

    /**
     * Creates an ACH transfer.
     *
     * @param accountNumber      the member account number of the share which the transfer is originating from
     * @param eftCreatableFields contains the transfer details such as the amount,
     *                           source and destination of the transfer
     *
     * @return an EftCreateResponse containing the transfer locator
     */
    public EftCreateResponse createAchTransfer(String accountNumber, EftCreatableFields eftCreatableFields) {

        CreateEftRequest request = new CreateEftRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(SymitarUtils.leftPadAccountNumber(accountNumber));
        request.setEftCreatableFields(eftCreatableFields);

        log.debug("Invoking createEft with request: {}", SymitarUtils.toXmlString(request));
        return accountService.createEft(request);
    }

    /**
     * Updates an ACH transfer.
     *
     * @param accountNumber               the member account number of the share which the transfer is originating from
     * @param eftLocator                  the unique identifier of the ACH transfer record
     * @param eftTransferUpdateableFields contains the transfer details to be updated such as the amount,
     *                                    source and destination of the transfer
     *
     * @return an EftUpdateByIDResponse containing the status of the update
     */
    public EftUpdateByIDResponse updateAchTransfer(
        String accountNumber,
        int eftLocator,
        EftUpdateableFields eftTransferUpdateableFields) {

        UpdateEftByIDRequest request = new UpdateEftByIDRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setEftLocator(eftLocator);
        request.setAccountNumber(SymitarUtils.leftPadAccountNumber(accountNumber));
        request.setEftUpdateableFields(eftTransferUpdateableFields);

        log.debug("Invoking updateEftByID with request: {}", SymitarUtils.toXmlString(request));
        return accountService.updateEftByID(request);
    }
}
