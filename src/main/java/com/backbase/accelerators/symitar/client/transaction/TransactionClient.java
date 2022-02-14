package com.backbase.accelerators.symitar.client.transaction;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.account.AccountService;
import com.symitar.generated.symxchange.account.LoanHoldPagedListRequest;
import com.symitar.generated.symxchange.account.LoanHoldPagedListResponse;
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsRequest;
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsResponse;
import com.symitar.generated.symxchange.account.LoanTransactionPagedListRequest;
import com.symitar.generated.symxchange.account.LoanTransactionPagedListResponse;
import com.symitar.generated.symxchange.account.LoanTransactionSearchPagedSelectFieldsRequest;
import com.symitar.generated.symxchange.account.LoanTransactionSearchPagedSelectFieldsResponse;
import com.symitar.generated.symxchange.account.ShareHoldPagedListRequest;
import com.symitar.generated.symxchange.account.ShareHoldPagedListResponse;
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsRequest;
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsResponse;
import com.symitar.generated.symxchange.account.ShareTransactionPagedListRequest;
import com.symitar.generated.symxchange.account.ShareTransactionPagedListResponse;
import com.symitar.generated.symxchange.account.ShareTransactionSearchPagedSelectFieldsRequest;
import com.symitar.generated.symxchange.account.ShareTransactionSearchPagedSelectFieldsResponse;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanHoldSingleSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanTransactionSingleSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareHoldSingleSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransactionSingleSelectableFields;
import com.symitar.generated.symxchange.common.dto.common.PagingRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Slf4j
public class TransactionClient {

    private final SymitarRequestSettings symitarRequestSettings;
    private final AccountService accountService;

    public TransactionClient(AccountService accountService, SymitarRequestSettings symitarRequestSettings) {
        Objects.requireNonNull(
            accountService,
            "TransactionClient cannot be initialized because AccountService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "TransactionClient cannot be initialized because SymitarRequestSettings is null.");

        this.accountService = accountService;
        this.symitarRequestSettings = symitarRequestSettings;
    }

    /**
     * Returns a paginated list of share transactions.
     *
     * @param accountNumber the member account number
     * @param shareId       the identifier of the share
     * @param token         the pagination token specifying where to start the retrieval from
     * @param pageSize      the page size to return
     * @return
     */
    public ShareTransactionPagedListResponse getShareTransactions(
        String accountNumber,
        String shareId,
        String token,
        int pageSize) {

        ShareTransactionPagedListRequest request = new ShareTransactionPagedListRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setShareId(shareId);
        request.setPagingRequestContext(getPagingRequestContext(token, pageSize));

        log.debug("Invoking getShareTransactionPagedList with request: {}", SymitarUtils.toXmlString(request));
        return accountService.getShareTransactionPagedList(request);
    }

    /**
     * Returns a paginated list of share transactions with an optional search filter.
     *
     * @param accountNumber          the member account number
     * @param shareId                the identifier of the share
     * @param token                  the pagination token specifying where to start the retrieval from
     * @param pageSize               the page size to return
     * @param shareTransactionFilter the search filter used to restrict which transactions are returned
     * @return
     */
    public ShareTransactionSearchPagedSelectFieldsResponse searchShareTransactions(
        String accountNumber,
        String shareId,
        String token,
        int pageSize,
        String shareTransactionFilter) {

        ShareTransactionSearchPagedSelectFieldsRequest request =
            initializeShareTransactionSearchPagedSelectFieldsRequest(
                symitarRequestSettings,
                accountNumber,
                shareId,
                token,
                pageSize
            );

        ShareTransactionSingleSelectableFields selectableFields = new ShareTransactionSingleSelectableFields();
        selectableFields.setIncludeAllShareTransactionFields(true);

        request.setSelectableFields(selectableFields);

        if (StringUtils.isNotBlank(shareTransactionFilter)) {
            request.setQuery(shareTransactionFilter);
        }

        log.debug("Invoking searchShareTransactionPagedSelectFields with request: {}",
            SymitarUtils.toXmlString(request));

        return accountService.searchShareTransactionPagedSelectFields(request);
    }

    /**
     * Returns a paginated list of share hold transactions.
     *
     * @param accountNumber the member account number
     * @param shareId       the identifier of the share
     * @param token         the pagination token specifying where to start the retrieval from
     * @param pageSize      the page size to return
     * @return
     */
    public ShareHoldPagedListResponse getShareHolds(
        String accountNumber,
        String shareId,
        String token,
        int pageSize) {

        ShareHoldPagedListRequest request = new ShareHoldPagedListRequest();
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setAccountNumber(accountNumber);
        request.setShareId(shareId);
        request.setPagingRequestContext(getPagingRequestContext(token, pageSize));

        log.debug("Invoking getShareHoldPagedList with request: {}", SymitarUtils.toXmlString(request));
        return accountService.getShareHoldPagedList(request);
    }

    /**
     * Returns a paginated list of share hold transactions with an optional search filter.
     *
     * @param accountNumber   the member account number
     * @param shareId         the identifier of the share
     * @param token           the pagination token specifying where to start the retrieval from
     * @param pageSize        the page size to return
     * @param shareHoldFilter the search filter used to restrict which transactions are returned
     * @return
     */
    public ShareHoldSearchPagedSelectFieldsResponse searchShareHolds(
        String accountNumber,
        String shareId,
        String token,
        int pageSize,
        String shareHoldFilter) {

        ShareHoldSearchPagedSelectFieldsRequest request = initializeShareHoldSearchPagedSelectFieldsRequest(
            symitarRequestSettings,
            accountNumber,
            shareId,
            token,
            pageSize
        );

        ShareHoldSingleSelectableFields shareHoldSingleSelectableFields = new ShareHoldSingleSelectableFields();
        shareHoldSingleSelectableFields.setIncludeAllShareHoldFields(true);

        request.setSelectableFields(shareHoldSingleSelectableFields);

        if (StringUtils.isNotBlank(shareHoldFilter)) {
            request.setQuery(shareHoldFilter);
        }

        log.debug("Invoking searchShareHoldPagedSelectFields with request: {}", SymitarUtils.toXmlString(request));
        return accountService.searchShareHoldPagedSelectFields(request);
    }

    /**
     * Returns a paginated list of loan transactions.
     *
     * @param accountNumber the member account number
     * @param loanId        the identifier of the loan
     * @param token         the pagination token specifying where to start the retrieval from
     * @param pageSize      the page size to return
     * @return
     */
    public LoanTransactionPagedListResponse getLoanTransactions(
        String accountNumber,
        String loanId,
        String token,
        int pageSize) {

        LoanTransactionPagedListRequest request = new LoanTransactionPagedListRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setLoanId(loanId);
        request.setPagingRequestContext(getPagingRequestContext(token, pageSize));

        log.debug("Invoking getLoanTransactionPagedList with request: {}", SymitarUtils.toXmlString(request));
        return accountService.getLoanTransactionPagedList(request);
    }

    /**
     * Returns a paginated list of loan transactions with an optional search filter.
     *
     * @param accountNumber         the member account number
     * @param loanId                the identifier of the loan
     * @param token                 the pagination token specifying where to start the retrieval from
     * @param pageSize              the page size to return
     * @param loanTransactionFilter the search filter use to restrict which transactions are returned
     * @return
     */
    public LoanTransactionSearchPagedSelectFieldsResponse searchLoanTransactions(
        String accountNumber,
        String loanId,
        String token,
        int pageSize,
        String loanTransactionFilter) {

        LoanTransactionSearchPagedSelectFieldsRequest request = initializeLoanTransactionSearchPagedSelectFieldsRequest(
            symitarRequestSettings,
            accountNumber,
            loanId,
            token,
            pageSize);

        LoanTransactionSingleSelectableFields loanTransactionSingleSelectableFields =
            new LoanTransactionSingleSelectableFields();

        loanTransactionSingleSelectableFields.setIncludeAllLoanTransactionFields(true);
        request.setSelectableFields(loanTransactionSingleSelectableFields);

        if (StringUtils.isNotBlank(loanTransactionFilter)) {
            request.setQuery(loanTransactionFilter);
        }

        log.debug("Invoking searchLoanTransactionPagedSelectFields with request: {}",
            SymitarUtils.toXmlString(request));

        return accountService.searchLoanTransactionPagedSelectFields(request);
    }

    /**
     * Returns a paginated list of loan hold transactions.
     *
     * @param accountNumber the member account number
     * @param loanId       the identifier of the loan
     * @param token         the pagination token specifying where to start the retrieval from
     * @param pageSize      the page size to return
     * @return
     */
    public LoanHoldPagedListResponse getLoanHolds(
        String accountNumber,
        String loanId,
        String token,
        int pageSize) {

        LoanHoldPagedListRequest request = new LoanHoldPagedListRequest();
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setAccountNumber(accountNumber);
        request.setLoanId(loanId);
        request.setPagingRequestContext(getPagingRequestContext(token, pageSize));

        log.debug("Invoking getLoanHoldPagedList with request: {}", SymitarUtils.toXmlString(request));
        return accountService.getLoanHoldPagedList(request);
    }

    /**
     * Returns a paginated list of loan hold transactions with an optional search filter.
     *
     * @param accountNumber  the member account number
     * @param loanId         the identifier of the loan
     * @param token          the pagination token
     * @param pageSize       the page size to return
     * @param loanHoldFilter the search filter use to restrict which transactions are returned
     * @return
     */
    public LoanHoldSearchPagedSelectFieldsResponse searchLoanHolds(
        String accountNumber,
        String loanId,
        String token,
        int pageSize,
        String loanHoldFilter) {

        LoanHoldSearchPagedSelectFieldsRequest request = initializeLoanHoldSearchPagedSelectFieldsRequest(
            symitarRequestSettings,
            accountNumber,
            loanId,
            token,
            pageSize);

        LoanHoldSingleSelectableFields loanHoldSingleSelectableFields = new LoanHoldSingleSelectableFields();
        loanHoldSingleSelectableFields.setIncludeAllLoanHoldFields(true);
        request.setSelectableFields(loanHoldSingleSelectableFields);

        if (StringUtils.isNotBlank(loanHoldFilter)) {
            request.setQuery(loanHoldFilter);
        }

        log.debug("Invoking searchLoanHoldPagedSelectFields with request: {}", SymitarUtils.toXmlString(request));
        return accountService.searchLoanHoldPagedSelectFields(request);
    }

    private ShareTransactionSearchPagedSelectFieldsRequest initializeShareTransactionSearchPagedSelectFieldsRequest(
        SymitarRequestSettings symitarRequestSettings,
        String accountNumber,
        String shareId,
        String token,
        int pageSize) {

        ShareTransactionSearchPagedSelectFieldsRequest request = new ShareTransactionSearchPagedSelectFieldsRequest();
        request.setAccountNumber(accountNumber);
        request.setShareId(shareId);
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setPagingRequestContext(getPagingRequestContext(token, pageSize));
        return request;
    }

    private ShareHoldSearchPagedSelectFieldsRequest initializeShareHoldSearchPagedSelectFieldsRequest(
        SymitarRequestSettings symitarRequestSettings,
        String accountNumber,
        String shareId,
        String token,
        int pageSize) {

        ShareHoldSearchPagedSelectFieldsRequest request = new ShareHoldSearchPagedSelectFieldsRequest();
        request.setAccountNumber(accountNumber);
        request.setShareId(shareId);
        request.setPagingRequestContext(getPagingRequestContext(token, pageSize));

        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        return request;
    }

    private LoanTransactionSearchPagedSelectFieldsRequest initializeLoanTransactionSearchPagedSelectFieldsRequest(
        SymitarRequestSettings symitarRequestSettings,
        String accountNumber,
        String loanId,
        String token,
        int pageSize) {

        LoanTransactionSearchPagedSelectFieldsRequest request = new LoanTransactionSearchPagedSelectFieldsRequest();
        request.setAccountNumber(accountNumber);
        request.setLoanId(loanId);
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setPagingRequestContext(getPagingRequestContext(token, pageSize));
        return request;
    }

    private LoanHoldSearchPagedSelectFieldsRequest initializeLoanHoldSearchPagedSelectFieldsRequest(
        SymitarRequestSettings symitarRequestSettings,
        String accountNumber,
        String loanId,
        String token,
        int pageSize) {

        LoanHoldSearchPagedSelectFieldsRequest request = new LoanHoldSearchPagedSelectFieldsRequest();
        request.setAccountNumber(accountNumber);
        request.setLoanId(loanId);
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setPagingRequestContext(getPagingRequestContext(token, pageSize));
        return request;
    }

    private PagingRequestContext getPagingRequestContext(String token, int pageSize) {
        PagingRequestContext pagingRequestContext = new PagingRequestContext();
        pagingRequestContext.setNumberOfRecordsToReturn(pageSize);

        if (StringUtils.isNotBlank(token)) {
            pagingRequestContext.setToken(token);
        } else {
            pagingRequestContext.setNumberOfRecordsToSkip(0);
        }

        return pagingRequestContext;
    }
}
