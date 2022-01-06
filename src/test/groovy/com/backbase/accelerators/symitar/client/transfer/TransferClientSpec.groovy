package com.backbase.accelerators.symitar.client.transfer

import com.backbase.accelerators.symitar.client.TestData
import com.backbase.accelerators.symitar.client.transfer.model.GetTransferListResponse
import com.backbase.accelerators.symitar.client.transfer.model.InitiateTransferRequest
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.CreateShareTransferRequest
import com.symitar.generated.symxchange.account.ExternalLoanTransferUpdateByIDResponse
import com.symitar.generated.symxchange.account.LoanTransferUpdateByIDResponse
import com.symitar.generated.symxchange.account.ShareTransferCreateResponse
import com.symitar.generated.symxchange.account.ShareTransferUpdateByIDResponse
import com.symitar.generated.symxchange.transactions.TransactionsService
import com.symitar.generated.symxchange.transactions.dto.TransactionsOverdrawInformationResponse
import com.symitar.generated.symxchange.transactions.dto.TransferRequest
import spock.lang.Specification

import javax.xml.datatype.DatatypeFactory
import java.time.LocalDate

class TransferClientSpec extends Specification {

    private AccountService accountService = Mock()
    private TransactionsService transactionsService = Mock()
    private TransferClient transferClient = new TransferClient(accountService, transactionsService, TestData.symitarRequestSettings)

    void 'getTransferList returns a list of share, loan, and external loan transfer records' () {
        given: 'an account number'
        String accountNumber = '000123453'

        when: 'paymentClient is invoked'
        GetTransferListResponse result = transferClient.getTransferList(accountNumber)

        then: 'accountService.getAccountSelectFieldsFilterChildren() is invoked 1 time'
        1 * accountService.getAccountSelectFieldsFilterChildren(_) >> TestData.accountSelectFieldsFilterChildrenResponse

        and:
        verifyAll(result.shareTransfers) {
            it[0].accountNumber == '621585'
            it[0].amount == 50.00
            it[0].day1 == 15
            it[0].effectiveDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2021-02-02').toString())
            it[0].expirationDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-02').toString())
            it[0].frequency == 4
            it[0].id == '00001'
            it[0].idType == 0
            it[0].locator == 1
            it[0].lastDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-15').toString())
            it[0].nextDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-03-15').toString())
            it[0].type == 3
        }

        verifyAll(result.loanTransfers) {
            it[0].accountNumber == '621585'
            it[0].amount == 500.00
            it[0].day1 == 15
            it[0].effectiveDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2021-02-02').toString())
            it[0].expirationDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-02').toString())
            it[0].frequency == 4
            it[0].id == '00002'
            it[0].idType == 1
            it[0].locator == 1
            it[0].lastDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-15').toString())
            it[0].nextDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-03-15').toString())
            it[0].type == 3
        }

        verifyAll(result.externalLoanTransfers) {
            it[0].accountNumber == '621585'
            it[0].amount == 500.00
            it[0].day1 == 15
            it[0].effectiveDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2021-02-02').toString())
            it[0].expirationDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-02').toString())
            it[0].frequency == 4
            it[0].id == '00003'
            it[0].idType == 3
            it[0].locator == 1
            it[0].lastDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-15').toString())
            it[0].nextDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-03-15').toString())
            it[0].type == 3
        }
    }

    void 'initiateScheduledShareTransfer submits a scheduled transfer to the core'() {
        given: 'a initiateShareTransferRequest'
        InitiateTransferRequest initiateTransferRequest = TestData.initiateTransferRequest_scheduledTransfer

        when: 'paymentClient is invoked'
        ShareTransferCreateResponse result = transferClient.initiateScheduledShareTransfer(initiateTransferRequest)

        then: 'the account service mock calls createShareTransfer exactly 1 time'
        1 * accountService.createShareTransfer(_ as CreateShareTransferRequest) >> TestData.shareTransferCreateResponse
        0 * transactionsService._

        and: 'The expected results are verified'
        verifyAll(result) {
            messageId == 'Success'
            shareTransferLocator == 1
        }
    }

    void 'initiateImmediateTransfer submits a immediate transfer to the core'() {
        given: 'a initiateShareTransferRequest'
        InitiateTransferRequest initiateTransferRequest = TestData.initiateTransferRequest_immediateTransfer

        when: 'paymentClient is invoked'
        TransactionsOverdrawInformationResponse result = transferClient.initiateImmediateTransfer(initiateTransferRequest)

        then: 'the transaction service mock calls transfer exactly 1 time'
        1 * transactionsService.transfer(_ as TransferRequest) >> TestData.transactionsOverdrawInformationResponse

        and: 'The expected results are verified'
        verifyAll(result) {
            statusMessage == 'Success'
            confirmation == '1'
        }
    }

    void 'cancelShareTransfer cancels a share transfer by updating the expiration date to todays date'() {
        given: 'An account number, shareId and transferLocation'
        String accountNumber = '00000645322'
        String shareId = '0030'
        int transferLocator = 293

        when: 'paymentClient is invoked'
        ShareTransferUpdateByIDResponse result = transferClient.cancelShareTransfer(
            accountNumber,
            shareId,
            transferLocator)

        then: 'the account service service mock calls updateShareTransferByID() exactly 1 time'
        1 * accountService.updateShareTransferByID(_) >> TestData.shareTransferUpdateByIDResponse

        and: 'The expected results are verified'
        verifyAll(result) {
            messageId == 'Test'
            updateStatus.isAllFieldsUpdateSuccess
        }
    }

    void 'cancelLoanTransfer cancels a loan transfer by updating the expiration date to todays date'() {
        given: 'An account number, loanId and transferLocation'
        String accountNumber = '00000645322'
        String loanId = '0030'
        int transferLocator = 293

        when: 'paymentClient is invoked'
        LoanTransferUpdateByIDResponse result = transferClient.cancelLoanTransfer(
            accountNumber,
            loanId,
            transferLocator)

        then: 'the account service service mock calls updateLoanTransferByID() exactly 1 time'
        1 * accountService.updateLoanTransferByID(_) >> TestData.loanTransferUpdateByIDResponse

        and: 'The expected results are verified'
        verifyAll(result) {
            messageId == 'Test'
            updateStatus.isAllFieldsUpdateSuccess
        }
    }

    void 'cancelExternalLoanTransfer cancels an external loan transfer by updating the expiration date to todays date'() {
        given: 'An account number, loanId and transferLocation'
        String accountNumber = '00000645322'
        int externalLoanLocator = 0030
        int transferLocator = 293

        when: 'paymentClient is invoked'
        ExternalLoanTransferUpdateByIDResponse result = transferClient.cancelExternalLoanTransfer(
            accountNumber,
            externalLoanLocator,
            transferLocator)

        then: 'the account service service mock calls updateExternalLoanTransferByID() exactly 1 time'
        1 * accountService.updateExternalLoanTransferByID(_) >> TestData.externalLoanTransferUpdateByIDResponse

        and: 'The expected results are verified'
        verifyAll(result) {
            messageId == 'Test'
            updateStatus.isAllFieldsUpdateSuccess
        }
    }
}

