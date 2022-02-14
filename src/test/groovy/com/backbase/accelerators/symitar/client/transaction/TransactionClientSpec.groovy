package com.backbase.accelerators.symitar.client.transaction

import com.backbase.accelerators.symitar.client.TestData
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.LoanHoldPagedListRequest
import com.symitar.generated.symxchange.account.LoanHoldPagedListResponse
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.LoanTransactionPagedListRequest
import com.symitar.generated.symxchange.account.LoanTransactionPagedListResponse
import com.symitar.generated.symxchange.account.LoanTransactionSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.LoanTransactionSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.ShareHoldPagedListRequest
import com.symitar.generated.symxchange.account.ShareHoldPagedListResponse
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.ShareTransactionPagedListRequest
import com.symitar.generated.symxchange.account.ShareTransactionPagedListResponse
import com.symitar.generated.symxchange.account.ShareTransactionSearchPagedSelectFieldsRequest
import com.symitar.generated.symxchange.account.ShareTransactionSearchPagedSelectFieldsResponse
import spock.lang.Specification

import javax.xml.datatype.DatatypeFactory
import java.time.LocalDate

class TransactionClientSpec extends Specification {

    AccountService accountService = Mock()
    TransactionClient transactionClient = new TransactionClient(accountService, TestData.symitarRequestSettings)

    void 'getShareTransactions returns a paginated transaction list for a given member'() {
        given: 'An accountNumber, shareId, pagination token and page size'
        String accountNumber = '621585'
        String shareId = '0001'
        String token = 'test'
        int pageSize = 20

        when: 'The account client is invoked'
        ShareTransactionPagedListResponse result = transactionClient.getShareTransactions(
            accountNumber,
            shareId,
            token,
            pageSize)

        then: 'The account service mock calls getShareTransactionPagedList exactly 1 time'
        1 * accountService.getShareTransactionPagedList(
            _ as ShareTransactionPagedListRequest) >> TestData.shareTransactionPagedListResponse

        and: 'The expected results are returned'
        verifyShareTransactions(result)
    }

    void 'searchShareTransactions returns a paginated transaction list for a given member'() {
        given: 'An accountNumber, shareId, pagination token, page size and search filter'
        String accountNumber = '621585'
        String shareId = '0001'
        String token = 'test'
        int pageSize = 20
        String shareTransactionFilter = 'filter'

        when: 'The account client is invoked'
        ShareTransactionSearchPagedSelectFieldsResponse result = transactionClient.searchShareTransactions(
            accountNumber,
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
        given: 'An accountNumber, shareId, pagination token, and page size'
        String accountNumber = '621585'
        String shareId = '0001'
        String token = 'test'
        int pageSize = 20

        when: 'The account client is invoked'
        ShareHoldPagedListResponse result = transactionClient.getShareHolds(
            accountNumber,
            shareId,
            token,
            pageSize)

        then: 'The account service mock calls getShareHoldPagedList exactly 1 time'
        1 * accountService.getShareHoldPagedList(
            _ as ShareHoldPagedListRequest) >> TestData.shareHoldPagedListResponse

        and: 'The expected results are returned'
        verifyShareHoldTransactions(result)
    }

    void 'searchShareHolds returns a paginated share hold transaction list for a given member'() {
        given: 'An accountNumber, shareId, pagination token, page size and search filter'
        String accountNumber = '621585'
        String shareId = '0001'
        String token = 'test'
        int pageSize = 20
        String shareHoldFilter = 'filter'

        when: 'The account client is invoked'
        ShareHoldSearchPagedSelectFieldsResponse result = transactionClient.searchShareHolds(
            accountNumber,
            shareId,
            token,
            pageSize,
            shareHoldFilter)

        then: 'The account service mock calls searchShareHoldPagedSelectFields exactly 1 time'
        1 * accountService.searchShareHoldPagedSelectFields(
            _ as ShareHoldSearchPagedSelectFieldsRequest) >> TestData.shareHoldSearchPagedSelectFieldsResponse

        and: 'The expected results are returned'
        verifyShareHoldTransactions(result)
    }

    void 'getLoanTransactions returns a paginated transaction list for a given member'() {
        given: 'An accountNumber, loanId, pagination token, and page size'
        String accountNumber = '621585'
        String loanId = '0002'
        String token = 'test'
        int pageSize = 20

        when: 'The account client is invoked'
        LoanTransactionPagedListResponse result = transactionClient.getLoanTransactions(
            accountNumber,
            loanId,
            token,
            pageSize)

        then: 'The account service mock calls getLoanTransactionPagedList exactly 1 time'
        1 * accountService.getLoanTransactionPagedList(
            _ as LoanTransactionPagedListRequest) >> TestData.loanTransactionPagedListResponse

        and: 'The expected results are returned'
        verifyLoanTransactions(result)
    }

    void 'searchLoanTransactions returns a paginated transaction list for a given member'() {
        given: 'An accountNumber, loanId, pagination token, page size and search filter'
        String accountNumber = '621585'
        String loanId = '0002'
        String token = 'test'
        int pageSize = 20
        String loanTransactionFilter = 'filter'

        when: 'The account client is invoked'
        LoanTransactionSearchPagedSelectFieldsResponse result = transactionClient.searchLoanTransactions(
            accountNumber,
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
        given: 'An accountNumber, loanId, pagination token, and page size'
        String accountNumber = '621585'
        String loanId = '0001'
        String token = 'test'
        int pageSize = 20

        when: 'The account client is invoked'
        LoanHoldPagedListResponse result = transactionClient.getLoanHolds(
            accountNumber,
            loanId,
            token,
            pageSize)

        then: 'The account service mock calls getLoanHoldPagedList exactly 1 time'
        1 * accountService.getLoanHoldPagedList(
            _ as LoanHoldPagedListRequest) >> TestData.loanHoldPagedListResponse

        and: 'The expected results are returned'
        verifyLoanHoldTransactions(result)
    }

    void 'searchLoanHolds returns a paginated loan hold transaction list for a given member'() {
        given: 'An accountNumber, loanId, pagination token, page size and search filter'
        String accountNumber = '621585'
        String loanId = '0001'
        String token = 'test'
        int pageSize = 20
        String loanHoldFilter = 'filter'

        when: 'The account client is invoked'
        LoanHoldSearchPagedSelectFieldsResponse result = transactionClient.searchLoanHolds(
            accountNumber,
            loanId,
            token,
            pageSize,
            loanHoldFilter)

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

    private void verifyShareTransactions(ShareTransactionPagedListResponse response) {
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

    private void verifyLoanTransactions(LoanTransactionPagedListResponse response) {
        verifyAll(response) {
            loanTransaction[0].id == '22222-22222'
            loanTransaction[0].accountNumber == '123456789'
            loanTransaction[0].activityDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            loanTransaction[0].description == 'Fake loan transaction record'
            loanTransaction[0].tranAmount == 100.00
        }
    }

    private void verifyShareHoldTransactions(ShareHoldPagedListResponse response) {
        verifyAll(response) {
            shareHold[0].achRecurringStop == 0
            shareHold[0].feeCode == 1
            shareHold[0].feeDescription == 'Fake share hold transaction record'
            shareHold[0].amount == 50.00
            shareHold[0].availableBalance == 100.00
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

    private void verifyLoanHoldTransactions(LoanHoldPagedListResponse response) {
        verifyAll(response) {
            loanHold[0].locator == 3
            loanHold[0].feeCode == 1
            loanHold[0].feeDescription == 'Fake loan hold transaction record'
            loanHold[0].amount == 33.00
            loanHold[0].availableBalance == 100.00
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
