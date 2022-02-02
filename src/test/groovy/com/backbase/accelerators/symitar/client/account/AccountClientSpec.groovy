package com.backbase.accelerators.symitar.client.account

import com.backbase.accelerators.symitar.client.TestData
import com.backbase.accelerators.symitar.client.account.model.GetProductsResponse
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenRequest
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.LoanRequest
import com.symitar.generated.symxchange.account.ShareRequest
import com.symitar.generated.symxchange.account.ShareUpdateByIDResponse
import com.symitar.generated.symxchange.account.UpdateShareByIDRequest
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoan
import com.symitar.generated.symxchange.account.dto.retrieve.Loan
import com.symitar.generated.symxchange.account.dto.retrieve.Share
import spock.lang.Specification

import javax.xml.datatype.DatatypeFactory
import java.time.LocalDate

class AccountClientSpec extends Specification {

    AccountService accountService = Mock()
    AccountClient accountClient = new AccountClient(accountService, TestData.symitarRequestSettings)

    void 'getLoansAndShares returns loan and share accounts for a given member'() {
        given: 'A memberId with share and loan filters'
        String accountNumber = '621585'
        String shareFilter = "CloseDate < {date: '1900-01-01'} AND ChargeOffDate < {date: '1900-01-01'}"
        String loanFilter = "CloseDate < {date: '1900-01-01'} AND ChargeOffDate < {date: '1900-01-01'}"
        String externalLoanFilter = "Type = 2 AND Status = 0"

        when: 'The account client is invoked'
        GetProductsResponse result = accountClient.getProducts(
            accountNumber,
            shareFilter,
            loanFilter,
            externalLoanFilter)

        then: 'The account service mock calls getAccountSelectFieldsFilterChildren exactly 1 time'
        1 * accountService.getAccountSelectFieldsFilterChildren(
            _ as AccountSelectFieldsFilterChildrenRequest) >> TestData.accountSelectFieldsFilterChildrenResponse

        and: 'The expected results are returned'
        verifyShares(result.shares)
        verifyLoans(result.loans)
        verifyExternalLoans(result.externalLoans)
    }

    void 'updateShare updates a share in the core' () {
        given: 'An accountNumber, a shareId'
        String accountNumber = '621585'
        String shareId = '0010'

        when: 'The account client is invoked'
        ShareUpdateByIDResponse result = accountClient.updateShare(accountNumber, shareId, TestData.shareUpdateableFields)

        then: 'The account service mock calls updateShareByID exactly 1 time'
        1 * accountService.updateShareByID(_ as UpdateShareByIDRequest) >> TestData.updateShareByIDResponse

        and: 'The expected results are returned'
        verifyAll(result) {
            result.messageId == 'Success'
            result.updateStatus.isAllFieldsUpdateSuccess
        }
    }

    void 'getShare returns a share with the given accountNumber and shareId' () {
        given: 'An accountNumber, a shareId'
        String accountNumber = '621585'
        String shareId = '0010'

        when: 'The account client is invoked'
        Share result = accountClient.getShare(accountNumber, shareId)

        then: 'The account service mock calls getShare exactly 1 time'
        1 * accountService.getShare(_ as ShareRequest) >> TestData.shareResponse

        and: 'The expected results are returned'
        verifyAll(result) {
            id == '00001'
            description == 'Fake share account'
            type == 0
            balance == 120.00
            availableBalance == 100.00
            micrAcctNumber == '01'
            openDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            regDCheckCount == 1
            regDTransferCount == 1
            divRate == 2.00
            maturityDate == null
        }
    }

    void 'getLoan returns a loan with the given accountNumber and loanId' () {
        given: 'An accountNumber, a loanId'
        String accountNumber = '621585'
        String loanId = '0010'

        when: 'The account client is invoked'
        Loan result = accountClient.getLoan(accountNumber, loanId)

        then: 'The account service mock calls getLoan exactly 1 time'
        1 * accountService.getLoan(_ as LoanRequest) >> TestData.loanResponse

        and: 'The expected results are returned'
        verifyAll(result) {
            id == '00002'
            description == 'Fake loan account'
            type == 1
            balance == 1000.00
            payment == 100.00
            paymentDue == 100.00
            dueDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            lastPaymentDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            openDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            interestRate == 6.00
            interestYtd == 50.00
            creditLimit == null
            availableCredit == null
            micrAcctNumber == '02'
            maturityDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
        }
    }

    private void verifyShares(List<Share> shareList) {
        verifyAll(shareList) {
            it[0].id == '00001'
            it[0].description == 'Fake share account'
            it[0].type == 0
            it[0].balance == 120.00
            it[0].availableBalance == 100.00
            it[0].micrAcctNumber == '01'
            it[0].openDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            it[0].regDCheckCount == 1
            it[0].regDTransferCount == 1
            it[0].divRate == 2.00
            it[0].maturityDate == null
        }
    }

    private void verifyLoans(List<Loan> loanList) {
        verifyAll(loanList) {
            it[0].id == '00002'
            it[0].description == 'Fake loan account'
            it[0].type == 1
            it[0].balance == 1000.00
            it[0].payment == 100.00
            it[0].paymentDue == 100.00
            it[0].dueDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            it[0].lastPaymentDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            it[0].openDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            it[0].interestRate == 6.00
            it[0].interestYtd == 50.00
            it[0].creditLimit == null
            it[0].availableCredit == null
            it[0].micrAcctNumber == '02'
            it[0].maturityDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
        }
    }

    private void verifyExternalLoans(List<ExternalLoan> externalLoanList) {
        verifyAll(externalLoanList) {
            it[0].accountNumber == '1234567890'
            it[0].description == 'Fake external loan account'
            it[0].closeDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())

        }
    }
}
