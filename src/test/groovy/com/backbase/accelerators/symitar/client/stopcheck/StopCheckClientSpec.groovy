package com.backbase.accelerators.symitar.client.stopcheck

import com.backbase.accelerators.symitar.client.TestData
import com.backbase.accelerators.symitar.client.exception.SymitarCoreClientException
import com.backbase.accelerators.symitar.client.stopcheck.model.StopCheckItem
import com.backbase.accelerators.symitar.client.stopcheck.model.StopCheckPaymentRequest
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenRequest
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.CreateLoanHoldRequest
import com.symitar.generated.symxchange.account.CreateShareHoldRequest
import com.symitar.generated.symxchange.account.LoanHoldCreateResponse
import com.symitar.generated.symxchange.account.ShareHoldCreateResponse
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.ShareHoldUpdateByIDResponse
import com.symitar.generated.symxchange.account.UpdateShareHoldByIDRequest
import com.symitar.generated.symxchange.transactions.TransactionsService
import com.symitar.generated.symxchange.transactions.dto.WithdrawFeeRequest
import spock.lang.Specification

import java.time.LocalDate

class StopCheckClientSpec extends Specification {

    private AccountService accountService = Mock()
    private TransactionsService transactionsService = Mock()
    private StopCheckClient stopCheckClient = new StopCheckClient(accountService, transactionsService, TestData.symitarRequestSettings)

    void 'stopShareCheckPayment halts the processing of a check payment'() {
        given: 'a stopCheckPaymentRequest'
        StopCheckPaymentRequest stopCheckPaymentRequest = TestData.stopCheckPaymentRequest

        when: 'stopCheckClient is invoked'
        ShareHoldCreateResponse result = stopCheckClient.stopShareCheckPayment(stopCheckPaymentRequest)

        then: 'the account service mock calls createShareHold exactly 1 time and the transaction service mock calls withdrawFee exactly 1 time'
        1 * accountService.createShareHold(_ as CreateShareHoldRequest) >> TestData.shareHoldCreateResponse
        1 * transactionsService.withdrawFee(_ as WithdrawFeeRequest) >> TestData.transactionsBaseResponse

        and: 'the expected results are verified'
        verifyAll(result) {
            messageId == 'Success'
            shareHoldLocator == 1
        }
    }

    void 'stopLoanCheckPayment halts the processing of a check payment for Loans'() {
        given: 'a stopCheckPaymentRequest'
        StopCheckPaymentRequest stopCheckPaymentRequest = TestData.stopCheckPaymentRequest

        when: 'stopCheckClient is invoked'
        LoanHoldCreateResponse result = stopCheckClient.stopLoanCheckPayment(stopCheckPaymentRequest)

        then: 'the account service mock calls createLoanHold exactly 1 time and the transaction service mock calls withdrawFee exactly 1 time'
        1 * accountService.createLoanHold(_ as CreateLoanHoldRequest) >> TestData.loanHoldCreateResponse
        1 * transactionsService.withdrawFee(_ as WithdrawFeeRequest) >> TestData.transactionsBaseResponse

        and: 'the expected results are verified'
        verifyAll(result) {
            messageId == 'Success'
            loanHoldLocator == 1
        }
    }

    void 'stopShareCheckPayment throws an exception on withdrawal fee failure'() {
        given: 'a stopCheckPaymentRequest'
        StopCheckPaymentRequest stopCheckPaymentRequest = TestData.stopCheckPaymentRequest

        when: 'stopCheckClient is invoked'
        stopCheckClient.stopShareCheckPayment(stopCheckPaymentRequest)

        then: 'the account service mock calls createShareHold 0 times and the transaction service mock calls withdrawFee exactly 1 time'
        0 * accountService.createShareHold(_ as CreateShareHoldRequest)
        1 * transactionsService.withdrawFee(_ as WithdrawFeeRequest) >> TestData.transactionsBaseResponse_withdrawalFeeFailure

        and:
        SymitarCoreClientException e = thrown()

        and: 'the expected results are verified'
        e.message == 'Failed to process withdrawal fee for stop check payment request'
    }

    void 'cancelStopCheckPayment cancels a pending stop check payment request'() {
        given: 'an account number, share id, and hold locator'
        String accountNumber = '000123453'
        String shareId = '0012'
        int holdLocator = 1

        when: 'stopCheckClient is invoked'
        ShareHoldUpdateByIDResponse result = stopCheckClient.cancelStopCheckPayment(accountNumber, shareId, holdLocator)

        then: 'the account service mock calls updateShareHoldByID exactly 1 time'
        1 * accountService.updateShareHoldByID(_ as UpdateShareHoldByIDRequest) >> TestData.shareHoldUpdateByIDResponse
        0 * transactionsService._

        and: 'the expected results are verified'
        verifyAll(result) {
            messageId == 'Success'
            updateStatus.isAllFieldsUpdateSuccess
        }
    }

    void 'getStopCheckPaymentList returns a list of stop check items with the provided account number'() {
        given: 'an account number'
        String accountNumber = '000123453'

        when: 'stopCheckClient is invoked'
        List<StopCheckItem> result = stopCheckClient.getStopCheckPaymentList(accountNumber)

        then:
        1 * accountService.getAccountSelectFieldsFilterChildren(
            _ as AccountSelectFieldsFilterChildrenRequest) >> TestData.accountSelectFieldsFilterChildrenResponse

        1 * accountService.searchShareHoldPagedSelectFields(
            _ as ShareHoldSearchPagedSelectFieldsRequest) >> TestData.shareHoldSearchPagedSelectFieldsResponse

        and:
        verifyAll(result) {
            it[0].productId == '00001'
            it[0].description == 'Fake share account'
            it[0].micrAccountNumber == '01'
            it[0].amount == 50.00
            it[0].holdLocator == 45
            it[0].dateSubmitted == LocalDate.parse('2020-03-02')
            it[0].effectiveDate == LocalDate.parse('2020-03-02')
            it[0].payeeName == 'John Doe'
            it[0].startingCheckNumber == '000223344'
            it[0].endingCheckNumber == '000443322'
            it[0].singleOrRange == 'R'
            it[0].status == 'Canceled'
        }
    }
}
