package com.backbase.accelerators.symitar.client.transfer;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.account.AccountClient;
import com.backbase.accelerators.symitar.client.account.model.GetProductsResponse;
import com.backbase.accelerators.symitar.client.transfer.model.GetTransferListResponse;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.account.AccountService;
import com.symitar.generated.symxchange.account.CreateExternalLoanTransferRequest;
import com.symitar.generated.symxchange.account.CreateShareTransferRequest;
import com.symitar.generated.symxchange.account.ExternalLoanTransferCreateResponse;
import com.symitar.generated.symxchange.account.ExternalLoanTransferUpdateByIDResponse;
import com.symitar.generated.symxchange.account.LoanTransferUpdateByIDResponse;
import com.symitar.generated.symxchange.account.ShareTransferCreateResponse;
import com.symitar.generated.symxchange.account.ShareTransferUpdateByIDResponse;
import com.symitar.generated.symxchange.account.UpdateExternalLoanTransferByIDRequest;
import com.symitar.generated.symxchange.account.UpdateLoanTransferByIDRequest;
import com.symitar.generated.symxchange.account.UpdateShareTransferByIDRequest;
import com.symitar.generated.symxchange.account.dto.create.ExternalLoanTransferCreatableFields;
import com.symitar.generated.symxchange.account.dto.create.ShareTransferCreatableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoan;
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanTransfer;
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanTransferList;
import com.symitar.generated.symxchange.account.dto.retrieve.Loan;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanTransfer;
import com.symitar.generated.symxchange.account.dto.retrieve.LoanTransferList;
import com.symitar.generated.symxchange.account.dto.retrieve.Share;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransfer;
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransferList;
import com.symitar.generated.symxchange.account.dto.update.ExternalLoanTransferUpdateableFields;
import com.symitar.generated.symxchange.account.dto.update.LoanTransferUpdateableFields;
import com.symitar.generated.symxchange.account.dto.update.ShareTransferUpdateableFields;
import com.symitar.generated.symxchange.transactions.TransactionsService;
import com.symitar.generated.symxchange.transactions.dto.TransactionsOverdrawInformationResponse;
import com.symitar.generated.symxchange.transactions.dto.TransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.backbase.accelerators.symitar.client.util.SymitarUtils.DateType.EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE;
import static com.backbase.accelerators.symitar.client.util.SymitarUtils.DateType.EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE;

@Slf4j
public class TransferClient {

    private final SymitarRequestSettings symitarRequestSettings;
    private final AccountService accountService;
    private final TransactionsService transactionsService;

    public TransferClient(
        AccountService accountService,
        TransactionsService transactionsService,
        SymitarRequestSettings symitarRequestSettings) {

        Objects.requireNonNull(
            accountService,
            "TransferClient cannot be initialized because AccountService is null.");

        Objects.requireNonNull(
            transactionsService,
            "TransferClient cannot be initialized because TransactionsService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "TransferClient cannot be initialized because SymitarRequestSettings is null.");

        this.accountService = accountService;
        this.transactionsService = transactionsService;
        this.symitarRequestSettings = symitarRequestSettings;
    }

    /**
     * Returns a list of share, loan and external loan transfer records for the given account number.
     *
     * <p>
     * This method first retrieves the shares and loans with the provided accountNumber. The shareFilter,
     * loanFilter and externalLoanFilter can be used to restrict what shares, loans and external loans come back.
     * </p>
     *
     * <p>
     * The transfer records are extracted for each share, loan, and external loan and returned
     * </p>
     *
     * @param accountNumber      the member account number
     * @param shareFilter        an optional search filter for limiting which shares are returned
     * @param loanFilter         an optional search filter for limiting which loans are returned
     * @param externalLoanFilter an optional search filter for limiting which external loans are returned
     * @return GetTransferListResponse containing a list of share, loan and external loan records.
     */
    public GetTransferListResponse getTransferList(
        String accountNumber,
        String shareFilter,
        String loanFilter,
        String externalLoanFilter) {

        // Transfer records are found on the share, loan and external loans, so fetch them first.
        GetProductsResponse getProductsResponse = getLoansAndShares(
            accountNumber,
            shareFilter,
            loanFilter,
            externalLoanFilter);

        List<ShareTransfer> shareTransfers = extractShareTransfers(getProductsResponse.getShares());
        List<LoanTransfer> loanTransfers = extractLoanTransfers(getProductsResponse.getLoans());

        List<ExternalLoanTransfer> externalLoanTransfers =
            extractExternalLoanTransfers(getProductsResponse.getExternalLoans());

        return GetTransferListResponse.builder()
            .shareTransfers(shareTransfers)
            .loanTransfers(loanTransfers)
            .externalLoanTransfers(externalLoanTransfers)
            .build();
    }

    /**
     * Creates a scheduled/recurring transfer from a share to another product.
     *
     * @param accountNumber                the member account number of the share which the transfer is originating from
     * @param shareId                      the identifier of the share which the transfer is originating from
     * @param shareTransferCreatableFields contains the transfer details such as the date,
     *                                     amount and destination of the transfer
     * @return a ShareTransferCreateResponse containing the transfer locator
     */
    public ShareTransferCreateResponse createRecurringOrScheduledShareTransfer(
        String accountNumber,
        String shareId,
        ShareTransferCreatableFields shareTransferCreatableFields) {

        CreateShareTransferRequest request = new CreateShareTransferRequest();
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setAccountNumber(accountNumber);
        request.setShareId(shareId);
        request.setShareTransferCreatableFields(shareTransferCreatableFields);

        log.debug("Invoking createShareTransfer with request: {}", SymitarUtils.toXmlString(request));
        return accountService.createShareTransfer(request);
    }

    /**
     * Creates an immediate one-time transfer from one product to another.
     *
     * @param transferRequest contains the transfer details such as the amount, source and destination of the transfer
     * @return a TransactionsOverdrawInformationResponse
     */
    public TransactionsOverdrawInformationResponse createImmediateTransfer(TransferRequest transferRequest) {
        transferRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        transferRequest.setMessageId(symitarRequestSettings.getMessageId());
        transferRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());

        log.debug("Invoking transfer with request: {}", SymitarUtils.toXmlString(transferRequest));
        return transactionsService.transfer(transferRequest);
    }

    /**
     * Creates scheduled/recurring transfer from an external loan (credit card) to another product.
     *
     * @param accountNumber                       the member account number
     * @param externalLoanLocator                 the identifier of the external loan
     * @param externalLoanTransferCreatableFields contains the transfer details such as the date,
     *                                            amount and destination of the transfer
     * @return a ExternalLoanTransferCreateResponse
     */
    public ExternalLoanTransferCreateResponse createRecurringOrScheduledExternalLoanTransfer(
        String accountNumber,
        int externalLoanLocator,
        ExternalLoanTransferCreatableFields externalLoanTransferCreatableFields) {

        CreateExternalLoanTransferRequest request = new CreateExternalLoanTransferRequest();
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setAccountNumber(leftPadAccountNumber(accountNumber));
        request.setExternalLoanLocator(externalLoanLocator);
        request.setExternalLoanTransferCreatableFields(externalLoanTransferCreatableFields);

        log.debug("Invoking createExternalLoanTransfer with request: {}", SymitarUtils.toXmlString(request));
        return accountService.createExternalLoanTransfer(request);
    }

    public ShareTransferUpdateByIDResponse cancelShareTransfer(
        String accountNumber,
        String shareId,
        int transferLocator) {

        /* Updating the transfer record by setting the expiration and effective date
        to today will effectively "cancel" the transfer. */
        JAXBElement<XMLGregorianCalendar> expirationDate = SymitarUtils.convertToXmlGregorianCalendar(
            LocalDate.now(),
            SymitarUtils.DateType.SHARE_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE);

        JAXBElement<XMLGregorianCalendar> effectiveDate = SymitarUtils.convertToXmlGregorianCalendar(
            LocalDate.now(),
            SymitarUtils.DateType.SHARE_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE);

        ShareTransferUpdateableFields shareTransferUpdateableFields = new ShareTransferUpdateableFields();
        shareTransferUpdateableFields.setExpirationDate(expirationDate);
        shareTransferUpdateableFields.setEffectiveDate(effectiveDate);

        UpdateShareTransferByIDRequest request = new UpdateShareTransferByIDRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setShareId(shareId);
        request.setShareTransferLocator(transferLocator);
        request.setShareTransferUpdateableFields(shareTransferUpdateableFields);

        log.debug("Invoking updateShareTransferByID with request: {}", SymitarUtils.toXmlString(request));
        return accountService.updateShareTransferByID(request);
    }

    public LoanTransferUpdateByIDResponse cancelLoanTransfer(
        String accountNumber,
        String loanId,
        int transferLocator) {

        /* Updating the transfer record by setting the expiration and effective date
        to today will effectively "cancel" the transfer. */
        JAXBElement<XMLGregorianCalendar> expirationDate = SymitarUtils.convertToXmlGregorianCalendar(
            LocalDate.now(),
            SymitarUtils.DateType.LOAN_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE);

        JAXBElement<XMLGregorianCalendar> effectiveDate = SymitarUtils.convertToXmlGregorianCalendar(
            LocalDate.now(),
            SymitarUtils.DateType.LOAN_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE);

        LoanTransferUpdateableFields loanTransferUpdateableFields = new LoanTransferUpdateableFields();
        loanTransferUpdateableFields.setExpirationDate(expirationDate);
        loanTransferUpdateableFields.setEffectiveDate(effectiveDate);

        UpdateLoanTransferByIDRequest request = new UpdateLoanTransferByIDRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setLoanId(loanId);
        request.setLoanTransferLocator(transferLocator);
        request.setLoanTransferUpdateableFields(loanTransferUpdateableFields);

        log.debug("Invoking updateLoanTransferByID with request: {}", SymitarUtils.toXmlString(request));
        return accountService.updateLoanTransferByID(request);
    }

    public ExternalLoanTransferUpdateByIDResponse cancelExternalLoanTransfer(
        String accountNumber,
        int externalLoanLocator,
        int transferLocator) {

        /* Updating the transfer record by setting the expiration and effective date
        to today will effectively "cancel" the transfer. */
        JAXBElement<XMLGregorianCalendar> expirationDate = SymitarUtils.convertToXmlGregorianCalendar(
            LocalDate.now(),
            EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE);

        JAXBElement<XMLGregorianCalendar> effectiveDate = SymitarUtils.convertToXmlGregorianCalendar(
            LocalDate.now(),
            EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE);

        ExternalLoanTransferUpdateableFields loanTransferUpdatableFields = new ExternalLoanTransferUpdateableFields();
        loanTransferUpdatableFields.setExpirationDate(expirationDate);
        loanTransferUpdatableFields.setEffectiveDate(effectiveDate);

        UpdateExternalLoanTransferByIDRequest request = new UpdateExternalLoanTransferByIDRequest();
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setAccountNumber(accountNumber);
        request.setExternalLoanLocator(externalLoanLocator);
        request.setExternalLoanTransferLocator(transferLocator);
        request.setExternalLoanTransferUpdateableFields(loanTransferUpdatableFields);

        log.debug("Invoking updateExternalLoanTransferByID with request: {}", SymitarUtils.toXmlString(request));
        return accountService.updateExternalLoanTransferByID(request);
    }

    private GetProductsResponse getLoansAndShares(
        String accountNumber,
        String shareFilter,
        String loanFilter,
        String externalLoanFilter) {

        return new AccountClient(accountService, symitarRequestSettings)
            .getProducts(accountNumber, shareFilter, loanFilter, externalLoanFilter);
    }

    private List<ShareTransfer> extractShareTransfers(List<Share> shares) {
        return shares.parallelStream()
            .map(Share::getShareTransferList)
            .filter(Objects::nonNull)
            .map(ShareTransferList::getShareTransfer)
            .flatMap(List::parallelStream)
            .peek(transfer -> log.debug("Fetched share transfer record: {}", SymitarUtils.toXmlString(transfer)))
            .collect(Collectors.toList());
    }

    private List<LoanTransfer> extractLoanTransfers(List<Loan> loans) {
        return loans.parallelStream()
            .map(Loan::getLoanTransferList)
            .filter(Objects::nonNull)
            .map(LoanTransferList::getLoanTransfer)
            .flatMap(List::parallelStream)
            .peek(transfer -> log.debug("Fetched loan transfer record: {}", SymitarUtils.toXmlString(transfer)))
            .collect(Collectors.toList());
    }

    private List<ExternalLoanTransfer> extractExternalLoanTransfers(List<ExternalLoan> externalLoans) {
        return externalLoans.stream()
            .map(ExternalLoan::getExternalLoanTransferList)
            .filter(Objects::nonNull)
            .map(ExternalLoanTransferList::getExternalLoanTransfer)
            .flatMap(List::parallelStream)
            .peek(transfer ->
                log.debug("Fetched external loan transfer record: {}", SymitarUtils.toXmlString(transfer)))
            .collect(Collectors.toList());
    }

    private String leftPadAccountNumber(String accountNumber) {
        if (StringUtils.isEmpty(accountNumber)) {
            return null;
        }

        return String.format("%010d", Integer.parseInt(accountNumber));
    }
}
