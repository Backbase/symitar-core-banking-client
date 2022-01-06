package com.backbase.accelerators.symitar.client.transaction

import com.backbase.accelerators.symitar.client.TestData
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.LoanTransactionSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.LoanTransactionSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.ShareTransactionSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.ShareTransactionSearchPagedSelectFieldsResponse
import spock.lang.Specification

import javax.xml.datatype.DatatypeFactory
import java.time.LocalDate

class TransactionClientSpec extends Specification {

    AccountService accountService = Mock()
    TransactionClient transactionClient = new TransactionClient(accountService, TestData.symitarRequestSettings)

    void 'getShareTransactions returns a paginated transaction list for a given member'() {
        given: 'A memberId with an external loan filter'
        String memberId = '621585'
        String shareId = '0001'
        String token = 'test'
        int pageSize = 20
        String shareTransactionFilter = 'filter'

        when: 'The account client is invoked'
        ShareTransactionSearchPagedSelectFieldsResponse result = transactionClient.getShareTransactions(
            memberId,
            shareId,
            token,
            pageSize,
            shareTransactionFilter)

        then: 'The account service mock calls searchShareTransactionPagedSelectFields exactly 1 time'
        1 * accountService.searchShareTransactionPagedSelectFields(
            _ as ShareTransactionSearchPagedSelectFieldsRequest) >> TestData.shareTransactionSearchPagedSelectFieldsResponse

        and: 'The expected results are returned'
        verifyShareTransactions(result)
    }

    void 'getShareHolds returns a paginated share hold transaction list for a given member'() {
        given: 'A memberId with an external loan filter'
        String memberId = '621585'
        String shareId = '0001'
        String token = 'test'
        int pageSize = 20
        String shareTransactionFilter = 'filter'

        when: 'The account client is invoked'
        ShareHoldSearchPagedSelectFieldsResponse result = transactionClient.getShareHolds(
            memberId,
            shareId,
            token,
            pageSize,
            shareTransactionFilter)

        then: 'The account service mock calls searchShareHoldPagedSelectFields exactly 1 time'
        1 * accountService.searchShareHoldPagedSelectFields(
            _ as ShareHoldSearchPagedSelectFieldsRequest) >> TestData.shareHoldSearchPagedSelectFieldsResponse

        and: 'The expected results are returned'
        verifyShareHoldTransactions(result)
    }

    void 'getLoanTransactions returns a paginated transaction list for a given member'() {
        given: 'A memberId with an external loan filter'
        String memberId = '621585'
        String loanId = '0002'
        String token = 'test'
        int pageSize = 20
        String loanTransactionFilter = 'filter'

        when: 'The account client is invoked'
        LoanTransactionSearchPagedSelectFieldsResponse result = transactionClient.getLoanTransactions(
            memberId,
            loanId,
            token,
            pageSize,
            loanTransactionFilter)

        then: 'The account service mock calls searchLoanTransactionPagedSelectFields exactly 1 time'
        1 * accountService.searchLoanTransactionPagedSelectFields(
            _ as LoanTransactionSearchPagedSelectFieldsRequest) >> TestData.loanTransactionSearchPagedSelectFieldsResponse

        and: 'The expected results are returned'
        verifyLoanTransactions(result)
    }

    void 'getLoanHolds returns a paginated loan hold transaction list for a given member'() {
        given: 'A memberId with an external loan filter'
        String memberId = '621585'
        String shareId = '0001'
        String token = 'test'
        int pageSize = 20
        String loanTransactionFilter = 'filter'

        when: 'The account client is invoked'
        LoanHoldSearchPagedSelectFieldsResponse result = transactionClient.getLoanHolds(
            memberId,
            shareId,
            token,
            pageSize,
            loanTransactionFilter)

        then: 'The account service mock calls searchLoanHoldPagedSelectFields exactly 1 time'
        1 * accountService.searchLoanHoldPagedSelectFields(
            _ as LoanHoldSearchPagedSelectFieldsRequest) >> TestData.loanHoldSearchPagedSelectFieldsResponse

        and: 'The expected results are returned'
        verifyLoanHoldTransactions(result)
    }

    private void verifyShareTransactions(ShareTransactionSearchPagedSelectFieldsResponse response) {
        verifyAll(response) {
            shareTransaction[0].id == '11111-11111'
            shareTransaction[0].accountNumber == '123456789'
            shareTransaction[0].activityDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            shareTransaction[0].description == 'Fake transaction record'
            shareTransaction[0].tranAmount == 10.00

        }
    }

    private void verifyLoanTransactions(LoanTransactionSearchPagedSelectFieldsResponse response) {
        verifyAll(response) {
            loanTransaction[0].id == '22222-22222'
            loanTransaction[0].accountNumber == '123456789'
            loanTransaction[0].activityDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            loanTransaction[0].description == 'Fake loan transaction record'
            loanTransaction[0].tranAmount == 100.00
        }
    }

    private void verifyShareHoldTransactions(ShareHoldSearchPagedSelectFieldsResponse response) {
        verifyAll(response) {
            shareHold[0].achRecurringStop == 0
            shareHold[0].feeCode == 1
            shareHold[0].feeDescription == 'Fake share hold transaction record'
            shareHold[0].amount == 50.00
            shareHold[0].availableBalance == 100.00
        }
    }

    private void verifyLoanHoldTransactions(LoanHoldSearchPagedSelectFieldsResponse response) {
        verifyAll(response) {
            loanHold[0].locator == 3
            loanHold[0].feeCode == 1
            loanHold[0].feeDescription == 'Fake loan hold transaction record'
            loanHold[0].amount == 33.00
            loanHold[0].availableBalance == 100.00
        }
    }
}
