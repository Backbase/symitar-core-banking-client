package com.backbase.accelerators.symitar.client.account;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.account.model.GetAccountRestrictionStatusResponse;
import com.backbase.accelerators.symitar.client.account.model.GetProductsResponse;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenRequest;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenResponse;
import com.symitar.generated.symxchange.account.AccountService;
import com.symitar.generated.symxchange.account.LoanUpdateByIDResponse;
import com.symitar.generated.symxchange.account.ShareUpdateByIDResponse;
import com.symitar.generated.symxchange.account.UpdateLoanByIDRequest;
import com.symitar.generated.symxchange.account.UpdateShareByIDRequest;
import com.symitar.generated.symxchange.account.dto.retrieve.Account;
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoan;
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanList;
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanTransferSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.Loan;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanList;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanTransferSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.Share;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareList;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransferSelectableFields;
import com.symitar.generated.symxchange.account.dto.update.LoanUpdateableFields;
import com.symitar.generated.symxchange.account.dto.update.ShareUpdateableFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class AccountClient {

    private final SymitarRequestSettings symitarRequestSettings;
    private final AccountService accountService;

    public AccountClient(AccountService accountService, SymitarRequestSettings symitarRequestSettings) {
        Objects.requireNonNull(
            accountService,
            "AccountClient cannot be initialized because AccountService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "AccountClient cannot be initialized because SymitarRequestSettings is null.");

        this.accountService = accountService;
        this.symitarRequestSettings = symitarRequestSettings;
    }

    /**
     * Retrieves a list of share, loan, and external loan products for the given member account number.
     *
     * @param accountNumber the member account number
     * @param shareFilter filter parameter to restrict which share products are returned
     * @param loanFilter filter parameter to restrict which loan products are returned
     * @param externalLoanFilter filter parameter to restrict which external loan products are returned
     *
     * @return a list of share, loan and external loan products.
     */
    public GetProductsResponse getProducts(
        String accountNumber,
        String shareFilter,
        String loanFilter,
        String externalLoanFilter) {

        AccountSelectFieldsFilterChildrenRequest request =
            SymitarUtils.initializeAccountSelectFieldsFilterChildrenRequest(symitarRequestSettings, accountNumber);

        if (StringUtils.isNotBlank(shareFilter)) {
            setShareFilterQuery(shareFilter, request);
        }

        if (StringUtils.isNotBlank(loanFilter)) {
            setLoanFilterQuery(loanFilter, request);
        }

        if (StringUtils.isNotBlank(externalLoanFilter)) {
            setExternalLoanFilterQuery(externalLoanFilter, request);
        }

        ShareTransferSelectableFields shareTransferSelectableFields = new ShareTransferSelectableFields();
        shareTransferSelectableFields.setIncludeAllShareTransferFields(true);

        LoanTransferSelectableFields loanTransferSelectableFields = new LoanTransferSelectableFields();
        loanTransferSelectableFields.setIncludeAllLoanTransferFields(true);

        ExternalLoanTransferSelectableFields externalLoanTransferSelectableFields =
            new ExternalLoanTransferSelectableFields();

        externalLoanTransferSelectableFields.setIncludeAllExternalLoanTransferFields(true);

        ShareSelectableFields shareSelectableFields = new ShareSelectableFields();
        shareSelectableFields.setShareTransferSelectableFields(shareTransferSelectableFields);
        shareSelectableFields.setIncludeAllShareFields(true);

        request.getSelectableFields().setShareSelectableFields(shareSelectableFields);

        LoanSelectableFields loanSelectableFields = new LoanSelectableFields();
        loanSelectableFields.setLoanTransferSelectableFields(loanTransferSelectableFields);
        loanSelectableFields.setIncludeAllLoanFields(true);

        request.getSelectableFields().setLoanSelectableFields(loanSelectableFields);

        ExternalLoanSelectableFields externalLoanSelectableFields = new ExternalLoanSelectableFields();
        externalLoanSelectableFields.setExternalLoanTransferSelectableFields(externalLoanTransferSelectableFields);
        externalLoanSelectableFields.setIncludeAllExternalLoanFields(false);

        request.getSelectableFields().setExternalLoanSelectableFields(externalLoanSelectableFields);

        log.debug("Invoking getAccountSelectFieldsFilterChildren with request: {}", SymitarUtils.toXmlString(request));
        return mapToGetProductsResponse(accountService.getAccountSelectFieldsFilterChildren(request));
    }

    /**
     * Updates the share name.
     *
     * @param accountNumber the member account number
     * @param shareId that identifier if the share
     * @param updatedName the updated name of the share
     *
     * @return
     */
    public ShareUpdateByIDResponse updateShareName(
        String accountNumber,
        String shareId,
        String updatedName) {

        UpdateShareByIDRequest updateShareByIdRequest = new UpdateShareByIDRequest();
        updateShareByIdRequest.setMessageId(symitarRequestSettings.getMessageId());
        updateShareByIdRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        updateShareByIdRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        updateShareByIdRequest.setShareId(shareId);
        updateShareByIdRequest.setAccountNumber(accountNumber);

        ShareUpdateableFields shareUpdateableFields = new ShareUpdateableFields();
        shareUpdateableFields.setDescription(updatedName);
        updateShareByIdRequest.setShareUpdateableFields(shareUpdateableFields);

        log.debug("Invoking updateShareByID with request: {}", SymitarUtils.toXmlString(updateShareByIdRequest));
        return accountService.updateShareByID(updateShareByIdRequest);
    }

    /**
     * Updates the loan name.
     *
     * @param accountNumber the member account number
     * @param loanId that identifier if the loan
     * @param updatedName the updated name of the loan
     *
     * @return
     */
    public LoanUpdateByIDResponse updateLoanName(
        String accountNumber,
        String loanId,
        String updatedName) {

        UpdateLoanByIDRequest updateLoanByIdRequest = new UpdateLoanByIDRequest();
        updateLoanByIdRequest.setMessageId(symitarRequestSettings.getMessageId());
        updateLoanByIdRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        updateLoanByIdRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        updateLoanByIdRequest.setLoanId(loanId);
        updateLoanByIdRequest.setAccountNumber(accountNumber);

        LoanUpdateableFields loanUpdateableFields = new LoanUpdateableFields();
        loanUpdateableFields.setDescription(updatedName);
        updateLoanByIdRequest.setLoanUpdateableFields(loanUpdateableFields);

        log.debug("Invoking updateLoanByID with request: {}", SymitarUtils.toXmlString(updateLoanByIdRequest));
        return accountService.updateLoanByID(updateLoanByIdRequest);
    }

    /**
     * Returns whether the account has a restriction imposed on it by checking the frozenMode property.
     * @param accountNumber the member account number
     * @return
     */
    public GetAccountRestrictionStatusResponse getAccountRestrictionStatus(String accountNumber) {

        AccountSelectFieldsFilterChildrenRequest request =
            SymitarUtils.initializeAccountSelectFieldsFilterChildrenRequest(symitarRequestSettings, accountNumber);

        log.debug("Invoking getAccountSelectFieldsFilterChildren with request: {}", SymitarUtils.toXmlString(request));
        return mapToGetAccountRestrictionStatusResponse(accountService.getAccountSelectFieldsFilterChildren(request));
    }

    private void setShareFilterQuery(String shareFilter, AccountSelectFieldsFilterChildrenRequest request) {
        ShareFilter sf = new ShareFilter();
        sf.setQuery(shareFilter);

        request.getChildrenSearchFilter().setShareFilter(sf);
    }

    private void setLoanFilterQuery(String loanFilter, AccountSelectFieldsFilterChildrenRequest request) {
        LoanFilter lf = new LoanFilter();
        lf.setQuery(loanFilter);

        request.getChildrenSearchFilter().setLoanFilter(lf);
    }

    private void setExternalLoanFilterQuery(String loanFilter, AccountSelectFieldsFilterChildrenRequest request) {
        ExternalLoanFilter lf = new ExternalLoanFilter();
        lf.setQuery(loanFilter);

        request.getChildrenSearchFilter().setExternalLoanFilter(lf);

    }

    private GetProductsResponse mapToGetProductsResponse(AccountSelectFieldsFilterChildrenResponse response) {
        Optional<Account> account = Optional.ofNullable(response.getAccount());

        Short accountType = account.map(Account::getType)
            .orElse(null);

        List<Share> shares = account.map(Account::getShareList)
            .map(ShareList::getShare)
            .orElse(Collections.emptyList());

        List<Loan> loans = account.map(Account::getLoanList)
            .map(LoanList::getLoan)
            .orElse(Collections.emptyList());

        List<ExternalLoan> externalLoans = account.map(Account::getExternalLoanList)
            .map(ExternalLoanList::getExternalLoan)
            .orElse(Collections.emptyList());

        GetProductsResponse getProductsResponse = new GetProductsResponse();
        getProductsResponse.setAccountType(accountType);
        getProductsResponse.setShares(shares);
        getProductsResponse.setLoans(loans);
        getProductsResponse.setExternalLoans(externalLoans);

        return getProductsResponse;
    }

    private GetAccountRestrictionStatusResponse mapToGetAccountRestrictionStatusResponse(
        AccountSelectFieldsFilterChildrenResponse response) {

        Short frozenMode = Optional.ofNullable(response)
            .map(AccountSelectFieldsFilterChildrenResponse::getAccount)
            .map(Account::getFrozenMode)
            .orElse((short) 0);

        GetAccountRestrictionStatusResponse getAccountRestrictionStatusResponse
            = new GetAccountRestrictionStatusResponse();

        getAccountRestrictionStatusResponse.setAccountFrozen(frozenMode != 0);
        return getAccountRestrictionStatusResponse;
    }
}
