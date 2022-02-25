package com.backbase.accelerators.symitar.client.account;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.account.model.GetAccountRestrictionStatusResponse;
import com.backbase.accelerators.symitar.client.account.model.GetElectronicStatementsStatusResponse;
import com.backbase.accelerators.symitar.client.account.model.GetProductsResponse;
import com.backbase.accelerators.symitar.client.account.model.GetTrackingRecordsResponse;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.account.AccountRequest;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenRequest;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenResponse;
import com.symitar.generated.symxchange.account.AccountService;
import com.symitar.generated.symxchange.account.LoanRequest;
import com.symitar.generated.symxchange.account.LoanUpdateByIDResponse;
import com.symitar.generated.symxchange.account.PreferenceListSelectFieldsRequest;
import com.symitar.generated.symxchange.account.PreferenceListSelectFieldsResponse;
import com.symitar.generated.symxchange.account.ShareRequest;
import com.symitar.generated.symxchange.account.ShareUpdateByIDResponse;
import com.symitar.generated.symxchange.account.UpdateLoanByIDRequest;
import com.symitar.generated.symxchange.account.UpdateShareByIDRequest;
import com.symitar.generated.symxchange.account.dto.retrieve.Account;
import com.symitar.generated.symxchange.account.dto.retrieve.Card;
import com.symitar.generated.symxchange.account.dto.retrieve.CardList;
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
import com.symitar.generated.symxchange.account.dto.retrieve.Preference;
import com.symitar.generated.symxchange.account.dto.retrieve.PreferenceAccessSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.PreferenceList;
import com.symitar.generated.symxchange.account.dto.retrieve.PreferenceSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.Share;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareList;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransferSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.Tracking;
import com.symitar.generated.symxchange.account.dto.retrieve.TrackingFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.TrackingList;
import com.symitar.generated.symxchange.account.dto.retrieve.TrackingSelectableFields;
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
     * <pre>
     * Retrieves a list of share, loan, and external loan products for the given member account number.
     * This method will also return:
     *
     * - A list of card records
     * - Account-level preferences
     * </pre>
     *
     * @param accountNumber      the member account number
     * @param shareFilter        filter parameter to restrict which share products are returned
     * @param loanFilter         filter parameter to restrict which loan products are returned
     * @param externalLoanFilter filter parameter to restrict which external loan products are returned
     * @return a list of share, loan, external loan and card products.
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

        PreferenceSelectableFields preferenceSelectableFields = new PreferenceSelectableFields();
        preferenceSelectableFields.setIncludeAllPreferenceFields(true);

        request.getSelectableFields().setPreferenceSelectableFields(preferenceSelectableFields);

        log.debug("Invoking getAccountSelectFieldsFilterChildren with request: {}", SymitarUtils.toXmlString(request));
        return mapToGetProductsResponse(accountService.getAccountSelectFieldsFilterChildren(request));
    }

    /**
     * Returns a single share with the given account number and share ID.
     *
     * @param accountNumber the member account number
     * @param shareId       the ID of the share
     * @return
     */
    public Share getShare(String accountNumber, String shareId) {
        ShareRequest shareRequest = new ShareRequest();
        shareRequest.setAccountNumber(accountNumber);
        shareRequest.setShareId(shareId);
        shareRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        shareRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        shareRequest.setMessageId(symitarRequestSettings.getMessageId());

        log.debug("Invoking getShare with request: {}", SymitarUtils.toXmlString(shareRequest));
        return accountService.getShare(shareRequest).getShare();
    }

    /**
     * Returns a single loan with the given account number and loan ID.
     *
     * @param accountNumber the member account number
     * @param loanId        the ID of the loan
     * @return
     */
    public Loan getLoan(String accountNumber, String loanId) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setAccountNumber(accountNumber);
        loanRequest.setLoanId(loanId);
        loanRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        loanRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        loanRequest.setMessageId(symitarRequestSettings.getMessageId());

        log.debug("Invoking getLoan with request: {}", SymitarUtils.toXmlString(loanRequest));
        return accountService.getLoan(loanRequest).getLoan();
    }

    /**
     * Updates a share in the core.
     *
     * @param accountNumber         the member account number
     * @param shareId               the unique identifier of the share product
     * @param shareUpdateableFields contains the properties of the share to be updated
     * @return
     */
    public ShareUpdateByIDResponse updateShare(
        String accountNumber,
        String shareId,
        ShareUpdateableFields shareUpdateableFields) {

        UpdateShareByIDRequest updateShareByIdRequest = new UpdateShareByIDRequest();
        updateShareByIdRequest.setMessageId(symitarRequestSettings.getMessageId());
        updateShareByIdRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        updateShareByIdRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        updateShareByIdRequest.setShareId(shareId);
        updateShareByIdRequest.setAccountNumber(accountNumber);
        updateShareByIdRequest.setShareUpdateableFields(shareUpdateableFields);

        log.debug("Invoking updateShareByID with request: {}", SymitarUtils.toXmlString(updateShareByIdRequest));
        return accountService.updateShareByID(updateShareByIdRequest);
    }

    /**
     * Updates a loan in the core.
     *
     * @param accountNumber        the member account number
     * @param loanId               the unique identifier of the share product
     * @param loanUpdateableFields contains the properties of the loan to be updated
     * @return
     */
    public LoanUpdateByIDResponse updateLoan(
        String accountNumber,
        String loanId,
        LoanUpdateableFields loanUpdateableFields) {

        UpdateLoanByIDRequest updateLoanByIdRequest = new UpdateLoanByIDRequest();
        updateLoanByIdRequest.setMessageId(symitarRequestSettings.getMessageId());
        updateLoanByIdRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        updateLoanByIdRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        updateLoanByIdRequest.setLoanId(loanId);
        updateLoanByIdRequest.setAccountNumber(accountNumber);
        updateLoanByIdRequest.setLoanUpdateableFields(loanUpdateableFields);

        log.debug("Invoking updateLoanByID with request: {}", SymitarUtils.toXmlString(updateLoanByIdRequest));
        return accountService.updateLoanByID(updateLoanByIdRequest);
    }

    /**
     * Returns whether the account has a restriction imposed on it by checking the frozenMode property.
     *
     * @param accountNumber the member account number
     * @return
     */
    public GetAccountRestrictionStatusResponse getAccountRestrictionStatus(String accountNumber) {

        AccountSelectFieldsFilterChildrenRequest request =
            SymitarUtils.initializeAccountSelectFieldsFilterChildrenRequest(symitarRequestSettings, accountNumber);

        log.debug("Invoking getAccountSelectFieldsFilterChildren with request: {}", SymitarUtils.toXmlString(request));
        return mapToGetAccountRestrictionStatusResponse(accountService.getAccountSelectFieldsFilterChildren(request));
    }

    /**
     * Returns the whether the account can access electronic statements.
     *
     * @param accountNumber the member account number
     * @return
     */
    public GetElectronicStatementsStatusResponse getElectronicStatementsStatus(String accountNumber) {
        AccountRequest request = new AccountRequest();
        request.setAccountNumber(accountNumber);
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());

        log.debug("Invoking getAccount with request: {}", SymitarUtils.toXmlString(request));
        Account account = accountService.getAccount(request).getAccount();

        boolean isElectronicStatementsEnabled = Optional.ofNullable(account.getEStmtEnable())
            .map(value -> value.equals((short) 1) || value.equals((short) 2))
            .orElse(false);

        GetElectronicStatementsStatusResponse response = new GetElectronicStatementsStatusResponse();
        response.setElectronicStatementsEnabled(isElectronicStatementsEnabled);
        return response;
    }

    public PreferenceListSelectFieldsResponse getLinkedAccounts(String accountNumber) {
        PreferenceListSelectFieldsRequest request = new PreferenceListSelectFieldsRequest();
        request.setAccountNumber(accountNumber);
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setSelectableFields(new PreferenceSelectableFields());

        PreferenceAccessSelectableFields preferenceAccessSelectableFields = new PreferenceAccessSelectableFields();
        preferenceAccessSelectableFields.setIncludeAllPreferenceAccessFields(true);
        request.getSelectableFields().setPreferenceAccessSelectableFields(preferenceAccessSelectableFields);

        log.debug("Invoking getPreferenceListSelectFields with request: {}", SymitarUtils.toXmlString(request));
        return accountService.getPreferenceListSelectFields(request);
    }

    /**
     * Returns a list of tracking records associated with the account.
     * @param accountNumber the member account number
     * @param trackingFilter an optional search filter that can be used to restrict which records are returned
     * @return
     */
    public GetTrackingRecordsResponse getTrackingRecords(String accountNumber, String trackingFilter) {
        AccountSelectFieldsFilterChildrenRequest request =
            SymitarUtils.initializeAccountSelectFieldsFilterChildrenRequest(symitarRequestSettings, accountNumber);

        if (StringUtils.isNotBlank(trackingFilter)) {
            setTrackingFilterQuery(trackingFilter, request);
        }

        TrackingSelectableFields trackingSelectableFields = new TrackingSelectableFields();
        trackingSelectableFields.setIncludeAllTrackingFields(true);

        request.getSelectableFields().setTrackingSelectableFields(trackingSelectableFields);

        log.debug("Invoking getAccountSelectFieldsFilterChildren with request: {}", SymitarUtils.toXmlString(request));
        return mapToGetTrackingRecordsResponse(accountService.getAccountSelectFieldsFilterChildren(request));
    }

    private void setShareFilterQuery(String shareFilter, AccountSelectFieldsFilterChildrenRequest request) {
        log.debug("Setting share search filter on AccountSelectFieldsFilterChildrenRequest: {}", shareFilter);

        ShareFilter sf = new ShareFilter();
        sf.setQuery(shareFilter);

        request.getChildrenSearchFilter().setShareFilter(sf);
    }

    private void setLoanFilterQuery(String loanFilter, AccountSelectFieldsFilterChildrenRequest request) {
        log.debug("Setting loan search filter on AccountSelectFieldsFilterChildrenRequest: {}", loanFilter);

        LoanFilter lf = new LoanFilter();
        lf.setQuery(loanFilter);

        request.getChildrenSearchFilter().setLoanFilter(lf);
    }

    private void setExternalLoanFilterQuery(String loanFilter, AccountSelectFieldsFilterChildrenRequest request) {
        log.debug("Setting external loan search filter on AccountSelectFieldsFilterChildrenRequest: {}", loanFilter);

        ExternalLoanFilter lf = new ExternalLoanFilter();
        lf.setQuery(loanFilter);

        request.getChildrenSearchFilter().setExternalLoanFilter(lf);

    }

    private void setTrackingFilterQuery(String trackingFilter, AccountSelectFieldsFilterChildrenRequest request) {
        log.debug("Setting tracking search filter on AccountSelectFieldsFilterChildrenRequest: {}", trackingFilter);

        TrackingFilter tf = new TrackingFilter();
        tf.setQuery(trackingFilter);

        request.getChildrenSearchFilter().setTrackingFilter(tf);

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

        List<Card> cards = account.map(Account::getCardList)
            .map(CardList::getCard)
            .orElse(Collections.emptyList());

        List<Preference> preferences = account.map(Account::getPreferenceList)
            .map(PreferenceList::getPreference)
            .orElse(Collections.emptyList());

        GetProductsResponse getProductsResponse = new GetProductsResponse();
        getProductsResponse.setAccountType(accountType);
        getProductsResponse.setShares(shares);
        getProductsResponse.setLoans(loans);
        getProductsResponse.setExternalLoans(externalLoans);
        getProductsResponse.setPreferences(preferences);
        getProductsResponse.setCards(cards);

        return getProductsResponse;
    }

    private GetTrackingRecordsResponse mapToGetTrackingRecordsResponse(
        AccountSelectFieldsFilterChildrenResponse response) {

        Optional<Account> account = Optional.ofNullable(response.getAccount());

        Short accountType = account.map(Account::getType)
            .orElse(null);

        List<Tracking> trackingRecords = account.map(Account::getTrackingList)
            .map(TrackingList::getTracking)
            .orElse(Collections.emptyList());

        GetTrackingRecordsResponse getTrackingRecordsResponse = new GetTrackingRecordsResponse();
        getTrackingRecordsResponse.setAccountType(accountType);
        getTrackingRecordsResponse.setTrackingRecords(trackingRecords);

        return getTrackingRecordsResponse;
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
