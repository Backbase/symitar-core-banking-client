package com.backbase.accelerators.symitar.client.transfer;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.account.AccountClient;
import com.backbase.accelerators.symitar.client.account.model.GetProductsResponse;
import com.backbase.accelerators.symitar.client.constants.Filters;
import com.backbase.accelerators.symitar.client.exception.SymitarCoreClientException;
import com.backbase.accelerators.symitar.client.transfer.model.GetTransferListResponse;
import com.backbase.accelerators.symitar.client.transfer.model.InitiateTransferRequest;
import com.backbase.accelerators.symitar.client.transfer.model.PaymentType;
import com.backbase.accelerators.symitar.client.transfer.model.ProductType;
import com.backbase.accelerators.symitar.client.transfer.model.TransferType;
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
import com.symitar.generated.symxchange.transactions.dto.DonorIdType;
import com.symitar.generated.symxchange.transactions.dto.RecipientIdType;
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
import static com.backbase.accelerators.symitar.client.util.SymitarUtils.DateType.EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_NEXT_DATE;

@Slf4j
public class TransferClient {

    public static final short SHARE = 0;
    public static final short LOAN = 1;

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
     * @param accountNumber the member account number
     * @return
     */
    public GetTransferListResponse getTransferList(String accountNumber) {
        GetProductsResponse getProductsResponse = getLoansAndShares(accountNumber);

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
     * Submits a scheduled/recurring transfer to the core.
     * @param initiateTransferRequest the request
     * @return
     */
    public ShareTransferCreateResponse initiateScheduledShareTransfer(InitiateTransferRequest initiateTransferRequest) {
        CreateShareTransferRequest request = new CreateShareTransferRequest();
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setShareId(initiateTransferRequest.getSourceProductId());
        request.setAccountNumber(leftPadAccountNumber(initiateTransferRequest.getSourceAccountNumber()));
        request.setShareTransferCreatableFields(createShareTransferCreatableFields(initiateTransferRequest));

        log.debug("Invoking createShareTransfer with request: {}", SymitarUtils.toXmlString(request));
        return accountService.createShareTransfer(request);
    }

    /**
     * Submits an immediate one-time transfer to the core.
     * @param request the request
     * @return
     */
    public TransactionsOverdrawInformationResponse initiateImmediateTransfer(InitiateTransferRequest request) {
        TransferRequest transferRequest = createImmediateTransferRequest(request);
        TransactionsOverdrawInformationResponse response = transactionsService.transfer(transferRequest);

        if (hasError(response)) {
            var stringBuilder = new StringBuilder()
                .append("sourceAccountNumber=").append(request.getSourceAccountNumber()).append("; ")
                .append("sourceShareId=").append(request.getSourceProductId()).append("; ")
                .append("destinationAccountNumber=").append(request.getDestinationAccountNumber()).append("; ")
                .append("destinationProductId=").append(request.getDestinationProductId()).append("; ")
                .append("destinationProductType=").append(request.getDestinationProductType()).append("; ")
                .append("amount=").append(request.getAmount()).append("; ")
                .append("symxErrorMessage=").append(response.getStatusMessage());

            log.error("Immediate transfer failed: {}", stringBuilder.toString());
            throw new SymitarCoreClientException(response.getStatusMessage());
        }

        return response;
    }

    /**
     * Submits an external loan (credit card) transfers to the core.
     * @param initiateTransferRequest the request
     * @return
     */
    public ExternalLoanTransferCreateResponse initiateExternalLoanTransfer(
        InitiateTransferRequest initiateTransferRequest) {

        ExternalLoanTransferCreatableFields externalLoanTransferCreatableFields
            = new ExternalLoanTransferCreatableFields();

        externalLoanTransferCreatableFields.setAccountNumber(
            leftPadAccountNumber(initiateTransferRequest.getSourceAccountNumber()));

        externalLoanTransferCreatableFields.setIdType(initiateTransferRequest.getSourceProductType().getValue());
        externalLoanTransferCreatableFields.setId(initiateTransferRequest.getSourceProductId());
        externalLoanTransferCreatableFields.setAmount(initiateTransferRequest.getAmount());

        externalLoanTransferCreatableFields.setEffectiveDate(
            SymitarUtils.convertToXmlGregorianCalendar(
                initiateTransferRequest.getEffectiveDate(),
                EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE));

        externalLoanTransferCreatableFields.setExpirationDate(
            SymitarUtils.convertToXmlGregorianCalendar(
                initiateTransferRequest.getExpirationDate(),
                EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE));

        externalLoanTransferCreatableFields.setFrequency(initiateTransferRequest.getFrequency());
        externalLoanTransferCreatableFields.setDay1(initiateTransferRequest.getDay1());
        externalLoanTransferCreatableFields.setDay2(initiateTransferRequest.getDay2());

        externalLoanTransferCreatableFields.setNextDate(
            SymitarUtils.convertToXmlGregorianCalendar(
                initiateTransferRequest.getEffectiveDate(),
                EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_NEXT_DATE));

        externalLoanTransferCreatableFields.setType(TransferType.OFF_CYCLE.getValue());

        CreateExternalLoanTransferRequest request = new CreateExternalLoanTransferRequest();
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setAccountNumber(leftPadAccountNumber(initiateTransferRequest.getDestinationAccountNumber()));
        request.setExternalLoanLocator(initiateTransferRequest.getExternalLoanLocator());
        request.setExternalLoanTransferCreatableFields(externalLoanTransferCreatableFields);

        log.debug("Invoking createExternalLoanTransfer with request: {}", SymitarUtils.toXmlString(request));
        return accountService.createExternalLoanTransfer(request);
    }

    public ShareTransferUpdateByIDResponse cancelShareTransfer(
        String accountNumber,
        String shareId,
        int transferLocator) {

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

    private TransferRequest createImmediateTransferRequest(InitiateTransferRequest request) {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        transferRequest.setMessageId(symitarRequestSettings.getMessageId());
        transferRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        transferRequest.setDonorAccountNumber(leftPadAccountNumber(request.getSourceAccountNumber()));
        transferRequest.setDonorId(request.getSourceProductId());
        transferRequest.setDonorType(getDonorIdType(request));
        transferRequest.setRecipientAccountNumber(leftPadAccountNumber(request.getDestinationAccountNumber()));
        transferRequest.setRecipientId(request.getDestinationProductId());
        transferRequest.setTransferAmount(request.getAmount());
        transferRequest.setRecipientType(getRecipientIdType(request));

        return transferRequest;
    }

    private DonorIdType getDonorIdType(InitiateTransferRequest request) {
        if (request.getSourceProductType() == ProductType.SHARE) {
            return DonorIdType.SHARE;
        }

        return DonorIdType.LOAN;
    }

    private RecipientIdType getRecipientIdType(InitiateTransferRequest initiateTransferRequest) {
        if (initiateTransferRequest.getDestinationProductType() == ProductType.SHARE) {
            return RecipientIdType.SHARE;
        }

        return RecipientIdType.LOAN;
    }

    private ShareTransferCreatableFields createShareTransferCreatableFields(InitiateTransferRequest request) {
        ShareTransferCreatableFields shareTransferCreatableFields = new ShareTransferCreatableFields();
        shareTransferCreatableFields.setAccountNumber(leftPadAccountNumber(request.getDestinationAccountNumber()));
        shareTransferCreatableFields.setAmount(request.getAmount());
        shareTransferCreatableFields.setFrequency(request.getFrequency());
        shareTransferCreatableFields.setId(request.getDestinationProductId());
        shareTransferCreatableFields.setIdType(request.getDestinationProductType().getValue());
        shareTransferCreatableFields.setDay1(request.getDay1());
        shareTransferCreatableFields.setDay1(request.getDay2());
        shareTransferCreatableFields.setType(TransferType.AUTO_SHARE_TRANSFER.getValue());

        shareTransferCreatableFields.setEffectiveDate(
            SymitarUtils.convertToXmlGregorianCalendar(request.getEffectiveDate()));

        shareTransferCreatableFields.setExpirationDate(
            SymitarUtils.convertToXmlGregorianCalendar(request.getExpirationDate()));

        shareTransferCreatableFields.setNextDate(
            SymitarUtils.convertToXmlGregorianCalendar(request.getNextDate()));

        if (request.getDestinationProductType() == ProductType.LOAN) {
            shareTransferCreatableFields.setPaymentType(PaymentType.STANDARD_PAYMENT.getValue());
        }

        return shareTransferCreatableFields;
    }

    private GetProductsResponse getLoansAndShares(String accountNumber) {
        return new AccountClient(accountService, symitarRequestSettings)
            .getProducts(
                accountNumber,
                Filters.SHARE_FILTER, Filters.LOAN_FILTER, Filters.EXTERNAL_LOAN_FILTER);
    }

    private List<ShareTransfer> extractShareTransfers(List<Share> shares) {
        return shares.parallelStream()
            .map(Share::getShareTransferList)
            .filter(Objects::nonNull)
            .map(ShareTransferList::getShareTransfer)
            .flatMap(List::parallelStream)
            .filter(shareTransfer -> shareTransfer.getType() == TransferType.AUTO_SHARE_TRANSFER.getValue())
            .peek(transfer -> log.debug("Fetched share transfer record: {}", SymitarUtils.toXmlString(transfer)))
            .collect(Collectors.toList());
    }

    private List<LoanTransfer> extractLoanTransfers(List<Loan> loans) {
        return loans.parallelStream()
            .map(Loan::getLoanTransferList)
            .filter(Objects::nonNull)
            .map(LoanTransferList::getLoanTransfer)
            .flatMap(List::parallelStream)
            .filter(loanTransfer -> loanTransfer.getType() == TransferType.AUTO_SHARE_TRANSFER.getValue())
            .peek(transfer -> log.debug("Fetched loan transfer record: {}", SymitarUtils.toXmlString(transfer)))
            .collect(Collectors.toList());
    }

    private List<ExternalLoanTransfer> extractExternalLoanTransfers(List<ExternalLoan> externalLoans) {
        return externalLoans.stream()
            .map(ExternalLoan::getExternalLoanTransferList)
            .filter(Objects::nonNull)
            .map(ExternalLoanTransferList::getExternalLoanTransfer)
            .flatMap(List::parallelStream)
            .filter(extLoanTransfer -> extLoanTransfer.getType() == TransferType.AUTO_SHARE_TRANSFER.getValue())
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

    private boolean hasError(TransactionsOverdrawInformationResponse response) {
        return Objects.nonNull(response)
            && response.getStatusCode() != 0
            && StringUtils.isEmpty(response.getConfirmation());
    }
}
