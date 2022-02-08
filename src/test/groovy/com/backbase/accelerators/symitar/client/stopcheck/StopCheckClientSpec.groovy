package com.backbase.accelerators.symitar.client.stopcheck

import com.backbase.accelerators.symitar.client.TestData
import com.backbase.accelerators.symitar.client.stopcheck.model.StopCheckItem
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenRequest
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.CreateLoanHoldRequest
import com.symitar.generated.symxchange.account.CreateShareHoldRequest
import com.symitar.generated.symxchange.account.LoanHoldCreateResponse
import com.symitar.generated.symxchange.account.ShareHoldCreateResponse
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.ShareHoldUpdateByIDResponse
import com.symitar.generated.symxchange.account.UpdateShareHoldByIDRequest
import com.symitar.generated.symxchange.account.dto.create.LoanHoldCreatableFields
import com.symitar.generated.symxchange.account.dto.create.ShareHoldCreatableFields
import com.symitar.generated.symxchange.transactions.TransactionsService
import com.symitar.generated.symxchange.transactions.dto.DonorIdType
import com.symitar.generated.symxchange.transactions.dto.TransactionsBaseResponse
import spock.lang.Specification

import java.time.LocalDate

class StopCheckClientSpec extends Specification {

    private AccountService accountService = Mock()
    private TransactionsService transactionsService = Mock()
    private StopCheckClient stopCheckClient = new StopCheckClient(accountService, transactionsService, TestData.symitarRequestSettings)

    void 'stopCheckPayment halts the processing of a share check payment'() {
        given: 'an accountNumber, a shareId a shareHoldCreatableFields object'
        String accountNumber = '518907'
        String shareId = '0010'
        ShareHoldCreatableFields shareHoldCreatableFields = TestData.shareHoldCreatableFields

        when: 'stopCheckClient is invoked'
        ShareHoldCreateResponse result = stopCheckClient.stopCheckPayment(accountNumber, shareId, shareHoldCreatableFields)

        then: 'the account service mock calls createShareHold exactly 1 time and the transaction service mock calls withdrawFee exactly 1 time'
        1 * accountService.createShareHold(_ as CreateShareHoldRequest) >> TestData.shareHoldCreateResponse

        and: 'the expected results are verified'
        verifyAll(result) {
            messageId == 'Success'
            shareHoldLocator == 1
        }
    }

    void 'stopCheckPayment halts the processing of a loan check payment'() {
        given: 'an accountNumber, a loanId a loanHoldCreatableFields object'
        String accountNumber = '518907'
        String loanId = '0070'
        LoanHoldCreatableFields loanHoldCreatableFields = TestData.loanHoldCreatableFields

        when: 'stopCheckClient is invoked'
        LoanHoldCreateResponse result = stopCheckClient.stopCheckPayment(accountNumber, loanId, loanHoldCreatableFields)

        then: 'the account service mock calls createLoanHold exactly 1 time and the transaction service mock calls withdrawFee exactly 1 time'
        1 * accountService.createLoanHold(_ as CreateLoanHoldRequest) >> TestData.loanHoldCreateResponse

        and: 'the expected results are verified'
        verifyAll(result) {
            messageId == 'Success'
            loanHoldLocator == 1
        }
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
        List<StopCheckItem> result = stopCheckClient.getStopCheckPayments(accountNumber, null, null, null, null)

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

    void 'doStopCheckPaymentFeeTransfer performs a withdrawal of a stop check payment fee' () {
        given: 'An accountNumber, donorId, donorType and effectiveDate'
        String accountNumber = '518907'
        String donorId = '0010'
        DonorIdType donorType = DonorIdType.SHARE

        when: 'stopCheckClient is invoked'
        TransactionsBaseResponse result = stopCheckClient.doStopCheckPaymentFeeTransfer(accountNumber, donorId, donorType)

        then: 'the transactionsService mock calls withdrawFee exactly 1 time'
        1 * transactionsService.withdrawFee(_) >> TestData.transactionsBaseResponse

        and: 'the results are verified'
        verifyAll(result) {
            messageId == 'Success'
            confirmation == 'Withdrawal confirmed'
        }
    }
}
