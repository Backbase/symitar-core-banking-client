package com.backbase.accelerators.symitar.client.stopcheck;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.constants.Filters;
import com.backbase.accelerators.symitar.client.stopcheck.model.CheckType;
import com.backbase.accelerators.symitar.client.stopcheck.model.StopCheckItem;
import com.backbase.accelerators.symitar.client.stopcheck.model.StopPayCode;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenRequest;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenResponse;
import com.symitar.generated.symxchange.account.AccountService;
import com.symitar.generated.symxchange.account.CreateLoanHoldRequest;
import com.symitar.generated.symxchange.account.CreateShareHoldRequest;
import com.symitar.generated.symxchange.account.LoanHoldCreateResponse;
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsRequest;
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsResponse;
import com.symitar.generated.symxchange.account.LoanHoldUpdateByIDResponse;
import com.symitar.generated.symxchange.account.ShareHoldCreateResponse;
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsRequest;
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsResponse;
import com.symitar.generated.symxchange.account.ShareHoldUpdateByIDResponse;
import com.symitar.generated.symxchange.account.UpdateLoanHoldByIDRequest;
import com.symitar.generated.symxchange.account.UpdateShareHoldByIDRequest;
import com.symitar.generated.symxchange.account.dto.create.LoanHoldCreatableFields;
import com.symitar.generated.symxchange.account.dto.create.ShareHoldCreatableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.Account;
import com.symitar.generated.symxchange.account.dto.retrieve.AccountChildrenFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.AccountSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.Loan;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanHold;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanHoldSingleSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanList;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.Share;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareHold;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareHoldSingleSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareList;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareSelectableFields;
import com.symitar.generated.symxchange.account.dto.update.LoanHoldUpdateableFields;
import com.symitar.generated.symxchange.account.dto.update.ShareHoldUpdateableFields;
import com.symitar.generated.symxchange.common.dto.common.PagingRequestContext;
import com.symitar.generated.symxchange.transactions.TransactionsService;
import com.symitar.generated.symxchange.transactions.dto.DonorIdType;
import com.symitar.generated.symxchange.transactions.dto.GLCodes;
import com.symitar.generated.symxchange.transactions.dto.TransactionsBaseResponse;
import com.symitar.generated.symxchange.transactions.dto.WithdrawFeeRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class StopCheckClient {

    private final SymitarRequestSettings symitarRequestSettings;
    private final AccountService accountService;
    private final TransactionsService transactionsService;

    public StopCheckClient(
        AccountService accountService,
        TransactionsService transactionsService,
        SymitarRequestSettings symitarRequestSettings) {

        Objects.requireNonNull(
            accountService,
            "StopCheckClient cannot be initialized because AccountService is null.");

        Objects.requireNonNull(
            transactionsService,
            "StopCheckClient cannot be initialized because TransactionsService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "StopCheckClient cannot be initialized because SymitarRequestSettings is null.");

        this.accountService = accountService;
        this.transactionsService = transactionsService;
        this.symitarRequestSettings = symitarRequestSettings;
    }

    /**
     * Submits a request to stop a check payment for a single check or a range of checks.
     *
     * @param accountNumber            the member account number
     * @param shareId                  the identifier of the share
     * @param shareHoldCreatableFields contains the properties to create a share hold for the stop payment
     * @return
     */
    public ShareHoldCreateResponse stopCheckPayment(
        String accountNumber,
        String shareId,
        ShareHoldCreatableFields shareHoldCreatableFields) {

        CreateShareHoldRequest request = new CreateShareHoldRequest();
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setShareId(shareId);
        request.setShareHoldCreatableFields(shareHoldCreatableFields);

        log.debug("Invoking createShareHold with request: {}", SymitarUtils.toXmlString(request));
        return accountService.createShareHold(request);
    }

    /**
     * Submits a request to stop a check payment for a single check or a range of checks.
     *
     * @param accountNumber           the member account number
     * @param loanId                  the identifier of the loan
     * @param loanHoldCreatableFields contains the properties to create a share hold for the stop payment
     * @return
     */
    public LoanHoldCreateResponse stopCheckPayment(
        String accountNumber,
        String loanId,
        LoanHoldCreatableFields loanHoldCreatableFields) {

        CreateLoanHoldRequest request = new CreateLoanHoldRequest();
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setLoanId(loanId);
        request.setLoanHoldCreatableFields(loanHoldCreatableFields);

        log.debug("Invoking createLoanHold with request: {}", SymitarUtils.toXmlString(request));
        return accountService.createLoanHold(request);
    }

    /**
     * Cancels a pending stop check payment request for a share.
     *
     * @param accountNumber    the member account number
     * @param shareId          the identifier of the share product from which the stop check request originated
     * @param shareHoldLocator the locator (identifier) of the share hold that was created for the stop check request
     * @return
     */
    public ShareHoldUpdateByIDResponse cancelStopCheckPayment(
        String accountNumber,
        String shareId,
        Integer shareHoldLocator) {

        // Updating the shareHold by setting an expiration date of today will effectively "cancel" the stop check
        ShareHoldUpdateableFields shareHoldUpdateableFields = new ShareHoldUpdateableFields();
        shareHoldUpdateableFields.setExpirationDate(SymitarUtils.convertToXmlGregorianCalendar(LocalDate.now()));

        UpdateShareHoldByIDRequest updateShareHoldByIdRequest = new UpdateShareHoldByIDRequest();
        updateShareHoldByIdRequest.setMessageId(symitarRequestSettings.getMessageId());
        updateShareHoldByIdRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        updateShareHoldByIdRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        updateShareHoldByIdRequest.setShareHoldLocator(shareHoldLocator);
        updateShareHoldByIdRequest.setShareId(shareId);
        updateShareHoldByIdRequest.setAccountNumber(accountNumber);
        updateShareHoldByIdRequest.setShareHoldUpdateableFields(shareHoldUpdateableFields);

        log.debug("Invoking updateShareHoldByID with request: {}",
            SymitarUtils.toXmlString(updateShareHoldByIdRequest));

        return accountService.updateShareHoldByID(updateShareHoldByIdRequest);
    }

    /**
     * Cancels a pending stop check payment request for a loan.
     *
     * @param accountNumber   the member account number
     * @param loanId          the identifier of the loan product from which the stop check request originated
     * @param loanHoldLocator the locator (identifier) of the loan hold that was created for the stop check request
     * @return
     */
    public LoanHoldUpdateByIDResponse cancelStopCheckLoanPayment(
        String accountNumber,
        String loanId,
        Integer loanHoldLocator) {

        // Updating the loanHold by setting an expiration date of today will effectively "cancel" the stop check
        LoanHoldUpdateableFields loanHoldUpdateableFields = new LoanHoldUpdateableFields();
        loanHoldUpdateableFields.setExpirationDate(SymitarUtils.convertToXmlGregorianCalendar(
            LocalDate.now(),
            SymitarUtils.DateType.LOAN_HOLD_UPDATABLE_FIELDS_EXPIRATION_DATE));

        UpdateLoanHoldByIDRequest updateLoanHoldByIdRequest = new UpdateLoanHoldByIDRequest();
        updateLoanHoldByIdRequest.setMessageId(symitarRequestSettings.getMessageId());
        updateLoanHoldByIdRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        updateLoanHoldByIdRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        updateLoanHoldByIdRequest.setLoanHoldLocator(loanHoldLocator);
        updateLoanHoldByIdRequest.setLoanId(loanId);
        updateLoanHoldByIdRequest.setAccountNumber(accountNumber);
        updateLoanHoldByIdRequest.setLoanHoldUpdateableFields(loanHoldUpdateableFields);

        log.debug("Invoking updateLoanHoldByID with request: {}", SymitarUtils.toXmlString(updateLoanHoldByIdRequest));
        return accountService.updateLoanHoldByID(updateLoanHoldByIdRequest);
    }

    /**
     * Returns a list of stop check payments items for shares and loans.
     *
     * @param accountNumber the member account number
     * @return a list of stop check items.
     */
    public List<StopCheckItem> getStopCheckPayments(String accountNumber) {
        log.debug("Getting stop check payment list for account number: {}", accountNumber);

        // First, fetch shares and loans
        Pair<List<Share>, List<Loan>> sharesAndLoans = getSharesAndLoans(accountNumber);
        List<Share> shares = sharesAndLoans.getLeft();
        List<Loan> loans = sharesAndLoans.getRight();

        List<StopCheckItem> stopCheckItems = new ArrayList<>();

        // Fetch the shareHold records associated with each share, then map them to a StopCheckItem
        shares.forEach(share -> {
            List<StopCheckItem> list = getShareHolds(accountNumber, share.getId())
                .map(ShareHoldSearchPagedSelectFieldsResponse::getShareHold)
                .stream()
                .flatMap(List::stream)
                .map(shareHold -> mapToStopCheckItem(shareHold, share))
                .collect(Collectors.toList());

            stopCheckItems.addAll(list);
        });

        // Fetch the loanHold records associated with each loan, then map them to a StopCheckItem
        loans.forEach(share -> {
            List<StopCheckItem> list = getLoanHolds(accountNumber, share.getId())
                .map(LoanHoldSearchPagedSelectFieldsResponse::getLoanHold)
                .stream()
                .flatMap(List::stream)
                .map(loanHold -> mapToStopCheckItem(loanHold, share))
                .collect(Collectors.toList());

            stopCheckItems.addAll(list);
        });

        return stopCheckItems;
    }

    /**
     * Performs a withdrawal of a stop check payment fee.
     *
     * @param accountNumber the member account number
     * @param donorId       the unique identifier of the share or loan from which the fee with be withdrawn from
     * @param donorType     the product type (SHARE, or LOAN)
     * @return
     */
    public TransactionsBaseResponse doStopCheckPaymentFeeTransfer(
        String accountNumber,
        String donorId,
        DonorIdType donorType) {

        WithdrawFeeRequest request = new WithdrawFeeRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setDonorId(donorId);
        request.setDonorType(donorType);
        request.setEffectiveDate(SymitarUtils.convertToXmlGregorianCalendar(LocalDate.now()).getValue());

        Optional.ofNullable(symitarRequestSettings.getStopCheckPaymentSettings()).ifPresent(
            stopCheckPaymentSettings -> {
                GLCodes glCodes = new GLCodes();
                glCodes.setClearingCode(stopCheckPaymentSettings.getGeneralLedgerClearingCode());

                request.setTotalAmount(stopCheckPaymentSettings.getWithdrawalFeeAmount());
                request.setFeeCode(stopCheckPaymentSettings.getWithdrawalFeeCode());
                request.setComment(stopCheckPaymentSettings.getWithdrawalFeeReasonText());
                request.setGLCodes(glCodes);
            }
        );

        log.debug("Invoking withdrawFee with request: {}", SymitarUtils.toXmlString(request));
        return transactionsService.withdrawFee(request);
    }

    private AccountSelectableFields createAccountSelectableFields() {
        ShareSelectableFields shareSelectableFields = new ShareSelectableFields();
        shareSelectableFields.setIncludeAllShareFields(true);

        LoanSelectableFields loanSelectableFields = new LoanSelectableFields();
        loanSelectableFields.setIncludeAllLoanFields(true);

        AccountSelectableFields accountSelectableFields = new AccountSelectableFields();
        accountSelectableFields.setShareSelectableFields(shareSelectableFields);
        accountSelectableFields.setLoanSelectableFields(loanSelectableFields);
        return accountSelectableFields;
    }

    private ShareHoldSearchPagedSelectFieldsRequest createGetShareHoldsRequest(String accountNumber, String shareId) {
        ShareHoldSearchPagedSelectFieldsRequest request = new ShareHoldSearchPagedSelectFieldsRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setShareId(shareId);

        ShareHoldSingleSelectableFields selectableFields = new ShareHoldSingleSelectableFields();
        selectableFields.setIncludeAllShareHoldFields(true);

        request.setSelectableFields(selectableFields);
        request.setQuery(Filters.STOP_CHECK_SHARE_HOLD_FILTER);

        PagingRequestContext pagingRequestContext = new PagingRequestContext();
        pagingRequestContext.setNumberOfRecordsToSkip(0);
        pagingRequestContext.setNumberOfRecordsToReturn(100);
        request.setPagingRequestContext(pagingRequestContext);

        return request;
    }

    private LoanHoldSearchPagedSelectFieldsRequest createGetLoanHoldsRequest(String accountNumber, String loanId) {
        LoanHoldSearchPagedSelectFieldsRequest request = new LoanHoldSearchPagedSelectFieldsRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setLoanId(loanId);

        LoanHoldSingleSelectableFields selectableFields = new LoanHoldSingleSelectableFields();
        selectableFields.setIncludeAllLoanHoldFields(true);

        request.setSelectableFields(selectableFields);
        request.setQuery(Filters.STOP_CHECK_SHARE_HOLD_FILTER);

        PagingRequestContext pagingRequestContext = new PagingRequestContext();
        pagingRequestContext.setNumberOfRecordsToSkip(0);
        pagingRequestContext.setNumberOfRecordsToReturn(100);
        request.setPagingRequestContext(pagingRequestContext);

        return request;
    }

    private Pair<List<Share>, List<Loan>> getSharesAndLoans(String accountNumber) {
        AccountChildrenFilter accountChildrenFilter = new AccountChildrenFilter();

        // Optionally set search filter to restrict the shares we get back from the core
        Optional.ofNullable(symitarRequestSettings.getStopCheckPaymentSettings())
            .map(SymitarRequestSettings.StopCheckPaymentSettings::getShareSearchFilter)
            .ifPresent(filter -> {
                ShareFilter shareFilter = new ShareFilter();
                shareFilter.setQuery(filter);

                accountChildrenFilter.setShareFilter(shareFilter);
            });

        // Optionally set search filter to restrict the loans we get back from the core
        Optional.ofNullable(symitarRequestSettings.getStopCheckPaymentSettings())
            .map(SymitarRequestSettings.StopCheckPaymentSettings::getLoanSearchFilter)
            .ifPresent(filter -> {
                LoanFilter loanFilter = new LoanFilter();
                loanFilter.setQuery(filter);

                accountChildrenFilter.setLoanFilter(loanFilter);
            });

        AccountSelectFieldsFilterChildrenRequest request =
            SymitarUtils.initializeAccountSelectFieldsFilterChildrenRequest(symitarRequestSettings, accountNumber);

        request.setSelectableFields(createAccountSelectableFields());
        request.setChildrenSearchFilter(accountChildrenFilter);

        log.debug("Invoking getAccountSelectFieldsFilterChildren with request: {}", SymitarUtils.toXmlString(request));
        AccountSelectFieldsFilterChildrenResponse response =
            accountService.getAccountSelectFieldsFilterChildren(request);

        List<Share> shares = Optional.ofNullable(response)
            .map(AccountSelectFieldsFilterChildrenResponse::getAccount)
            .map(Account::getShareList)
            .map(ShareList::getShare)
            .orElse(Collections.emptyList());

        List<Loan> loans = Optional.ofNullable(response)
            .map(AccountSelectFieldsFilterChildrenResponse::getAccount)
            .map(Account::getLoanList)
            .map(LoanList::getLoan)
            .orElse(Collections.emptyList());

        return Pair.of(shares, loans);
    }

    private Optional<ShareHoldSearchPagedSelectFieldsResponse> getShareHolds(String accountNumber, String shareId) {
        return Optional.ofNullable(
            accountService.searchShareHoldPagedSelectFields(createGetShareHoldsRequest(accountNumber, shareId)));
    }

    private Optional<LoanHoldSearchPagedSelectFieldsResponse> getLoanHolds(String accountNumber, String loanId) {
        return Optional.ofNullable(
            accountService.searchLoanHoldPagedSelectFields(createGetLoanHoldsRequest(accountNumber, loanId)));
    }

    private StopCheckItem mapToStopCheckItem(ShareHold shareHold, Share share) {
        StopCheckItem stopCheckItem = StopCheckItem.builder()
            .productId(share.getId())
            .description(share.getDescription())
            .micrAccountNumber(share.getMicrAcctNumber())
            .holdLocator(shareHold.getLocator())
            .amount(shareHold.getAmount())
            .dateSubmitted(SymitarUtils.toLocalDate(shareHold.getHoldCreationDate()))
            .effectiveDate(SymitarUtils.toLocalDate(shareHold.getEffectiveDate()))
            .expirationDate(SymitarUtils.toLocalDate(shareHold.getExpirationDate()))
            .payeeName(shareHold.getPayeeName())
            .startingCheckNumber(shareHold.getReference1())
            .stopPayCode(Integer.parseInt(shareHold.getStopPayCode().toString()))
            .status(getStopCheckStatus(shareHold))
            .feeCode(shareHold.getFeeCode())
            .feeDescription(shareHold.getFeeDescription())
            .type(shareHold.getType())
            .memberBranch(shareHold.getMemberBranch())
            .build();

        if (isBlank(shareHold.getReference2())) {
            stopCheckItem.setSingleOrRange(CheckType.SINGLE.getValue());
        } else {
            stopCheckItem.setEndingCheckNumber(shareHold.getReference2());
            stopCheckItem.setSingleOrRange(CheckType.RANGE.getValue());
        }

        if (shareHold.getStopPayCode() == StopPayCode.OTHER.getValue()) {
            if (isNotBlank(shareHold.getReference3()) && isNotBlank(shareHold.getReference4())) {
                stopCheckItem.setOtherReason(shareHold.getReference3().concat(shareHold.getReference4()));
            } else {
                stopCheckItem.setOtherReason(shareHold.getReference3());
            }
        }

        return stopCheckItem;
    }

    private StopCheckItem mapToStopCheckItem(LoanHold loanHold, Loan loan) {
        StopCheckItem stopCheckItem = StopCheckItem.builder()
            .productId(loan.getId())
            .description(loan.getDescription())
            .micrAccountNumber(loan.getMicrAcctNumber())
            .holdLocator(loanHold.getLocator())
            .amount(loanHold.getAmount())
            .dateSubmitted(SymitarUtils.toLocalDate(loanHold.getHoldCreationDate()))
            .effectiveDate(SymitarUtils.toLocalDate(loanHold.getEffectiveDate()))
            .expirationDate(SymitarUtils.toLocalDate(loanHold.getExpirationDate()))
            .payeeName(loanHold.getPayeeName())
            .startingCheckNumber(loanHold.getReference1())
            .stopPayCode(Integer.parseInt(loanHold.getStopPayCode().toString()))
            .status(getStopCheckStatus(loanHold))
            .feeCode(loanHold.getFeeCode())
            .feeDescription(loanHold.getFeeDescription())
            .type(loanHold.getType())
            .memberBranch(loanHold.getMemberBranch())
            .build();

        if (isBlank(loanHold.getReference2())) {
            stopCheckItem.setSingleOrRange(CheckType.SINGLE.getValue());
        } else {
            stopCheckItem.setEndingCheckNumber(loanHold.getReference2());
            stopCheckItem.setSingleOrRange(CheckType.RANGE.getValue());
        }

        if (loanHold.getStopPayCode() == StopPayCode.OTHER.getValue()) {
            if (isNotBlank(loanHold.getReference3()) && isNotBlank(loanHold.getReference4())) {
                stopCheckItem.setOtherReason(loanHold.getReference3().concat(loanHold.getReference4()));
            } else {
                stopCheckItem.setOtherReason(loanHold.getReference3());
            }
        }

        return stopCheckItem;
    }

    private String getStopCheckStatus(ShareHold shareHold) {
        XMLGregorianCalendar effectiveDatePlusOneYear = shareHold.getEffectiveDate();
        effectiveDatePlusOneYear.setYear(effectiveDatePlusOneYear.getYear() + 1);

        if (isStopCheckCanceled(shareHold.getExpirationDate(), effectiveDatePlusOneYear)) {
            return "Canceled";
        } else if (isStopCheckExpired(shareHold.getExpirationDate(), effectiveDatePlusOneYear)) {
            return "Expired";
        } else {
            return "Active";
        }
    }

    private String getStopCheckStatus(LoanHold loanHold) {
        XMLGregorianCalendar effectiveDatePlusOneYear = loanHold.getEffectiveDate();
        effectiveDatePlusOneYear.setYear(effectiveDatePlusOneYear.getYear() + 1);

        if (isStopCheckCanceled(loanHold.getExpirationDate(), effectiveDatePlusOneYear)) {
            return "Canceled";
        } else if (isStopCheckExpired(loanHold.getExpirationDate(), effectiveDatePlusOneYear)) {
            return "Expired";
        } else {
            return "Active";
        }
    }

    private boolean isStopCheckCanceled(
        XMLGregorianCalendar expirationDate,
        XMLGregorianCalendar effectiveDatePlusOneYear) {

        return expirationDate.compare(effectiveDatePlusOneYear) == DatatypeConstants.LESSER;
    }

    private boolean isStopCheckExpired(
        XMLGregorianCalendar expirationDate,
        XMLGregorianCalendar effectiveDatePlusOneYear) {

        return expirationDate.compare(effectiveDatePlusOneYear) == DatatypeConstants.EQUAL
            && expirationDate.toGregorianCalendar().compareTo(new GregorianCalendar()) <= 0;
    }
}
