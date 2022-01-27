package com.backbase.accelerators.symitar.client

import com.backbase.accelerators.symitar.client.name.model.UpdateNameRecordRequest
import com.backbase.accelerators.symitar.client.stopcheck.model.StopCheckPaymentRequest
import com.backbase.accelerators.symitar.client.stopcheck.model.StopPayCode
import com.backbase.accelerators.symitar.client.transfer.model.InitiateTransferRequest
import com.backbase.accelerators.symitar.client.transfer.model.ProductType
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenResponse
import com.symitar.generated.symxchange.account.ExternalLoanTransferUpdateByIDResponse
import com.symitar.generated.symxchange.account.LoanHoldCreateResponse
import com.symitar.generated.symxchange.account.LoanHoldSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.LoanResponse
import com.symitar.generated.symxchange.account.LoanTransactionSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.LoanTransferUpdateByIDResponse
import com.symitar.generated.symxchange.account.NameDeleteResponse
import com.symitar.generated.symxchange.account.NameUpdateByIDResponse
import com.symitar.generated.symxchange.account.ShareHoldCreateResponse
import com.symitar.generated.symxchange.account.ShareHoldSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.ShareHoldUpdateByIDResponse
import com.symitar.generated.symxchange.account.ShareResponse
import com.symitar.generated.symxchange.account.ShareTransactionSearchPagedSelectFieldsResponse
import com.symitar.generated.symxchange.account.ShareTransferCreateResponse
import com.symitar.generated.symxchange.account.ShareTransferUpdateByIDResponse
import com.symitar.generated.symxchange.account.ShareUpdateByIDResponse
import com.symitar.generated.symxchange.account.dto.retrieve.Account
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoan
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanList
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanTransfer
import com.symitar.generated.symxchange.account.dto.retrieve.ExternalLoanTransferList
import com.symitar.generated.symxchange.account.dto.retrieve.Loan
import com.symitar.generated.symxchange.account.dto.retrieve.LoanHold
import com.symitar.generated.symxchange.account.dto.retrieve.LoanList
import com.symitar.generated.symxchange.account.dto.retrieve.LoanTransaction
import com.symitar.generated.symxchange.account.dto.retrieve.LoanTransfer
import com.symitar.generated.symxchange.account.dto.retrieve.LoanTransferList
import com.symitar.generated.symxchange.account.dto.retrieve.Name
import com.symitar.generated.symxchange.account.dto.retrieve.NameList
import com.symitar.generated.symxchange.account.dto.retrieve.Share
import com.symitar.generated.symxchange.account.dto.retrieve.ShareHold
import com.symitar.generated.symxchange.account.dto.retrieve.ShareList
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransaction
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransfer
import com.symitar.generated.symxchange.account.dto.retrieve.ShareTransferList
import com.symitar.generated.symxchange.common.dto.common.AdminCredentialsChoice
import com.symitar.generated.symxchange.common.dto.common.AdministrativeCredentials
import com.symitar.generated.symxchange.common.dto.common.CredentialsChoice
import com.symitar.generated.symxchange.common.dto.common.DeviceInformation
import com.symitar.generated.symxchange.common.dto.common.UpdateStatus
import com.symitar.generated.symxchange.findby.dto.LookupByActiveCardResponse
import com.symitar.generated.symxchange.findby.dto.LookupBySSNResponse
import com.symitar.generated.symxchange.poweron.dto.ExecutionResponseBody
import com.symitar.generated.symxchange.poweron.dto.PowerOnExecutionResponse
import com.symitar.generated.symxchange.transactions.dto.TransactionsBaseResponse
import com.symitar.generated.symxchange.transactions.dto.TransactionsOverdrawInformationResponse

import javax.xml.datatype.DatatypeFactory
import java.time.LocalDate

class TestData {

    static SymitarRequestSettings symitarRequestSettings = new SymitarRequestSettings(
        baseUrl: 'http://10.0.111.21:8087/SymXchange/2020.00',
        messageId: 'Test',
        stopCheckPaymentWithdrawalFeeCode: 1,
        stopCheckPaymentWithdrawalFeeAmount: 100.00,

        deviceInformation: new DeviceInformation(
            deviceNumber: 20672,
            deviceType: 'BACKBASE'
        ),

        adminCredentialsChoice: new AdminCredentialsChoice(
            administrativeCredentials: new AdministrativeCredentials(
                password: 'BB1234-4321'
            )
        ),

        credentialsChoice: new CredentialsChoice(
            administrativeCredentials: new AdministrativeCredentials(
                password: 'BB1234-4321'
            )
        )
    )

    static AccountSelectFieldsFilterChildrenResponse accountSelectFieldsFilterChildrenResponse = new AccountSelectFieldsFilterChildrenResponse(
        messageId: 'Test',
        account: new Account(
            type: (short) 20,
            shareList: new ShareList(
                share: [
                    new Share(
                        id: '00001',
                        description: 'Fake share account',
                        type: 0,
                        balance: 120.00,
                        availableBalance: 100.00,
                        micrAcctNumber: '01',
                        openDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                        regDCheckCount: 1,
                        regDTransferCount: 1,
                        divRate: 2.00,
                        maturityDate: null,
                        shareTransferList: new ShareTransferList(
                            shareTransfer: [
                                new ShareTransfer(
                                    accountNumber: '621585',
                                    amount: 50.00,
                                    day1: 15,
                                    effectiveDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2021-02-02').toString()),
                                    expirationDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-02').toString()),
                                    frequency: 4,
                                    id: '00001',
                                    idType: 0,
                                    locator: 1,
                                    lastDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-15').toString()),
                                    nextDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-03-15').toString()),
                                    type: 3
                                )
                            ]
                        )
                    )
                ]
            ),
            loanList: new LoanList(
                loan: [
                    new Loan(
                        id: '00002',
                        description: 'Fake loan account',
                        type: 1,
                        balance: 1000.00,
                        payment: 100.00,
                        paymentDue: 100.00,
                        dueDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                        lastPaymentDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                        openDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                        interestRate: 6.00,
                        interestYtd: 50.00,
                        creditLimit: null,
                        availableCredit: null,
                        micrAcctNumber: '02',
                        maturityDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                        loanTransferList: new LoanTransferList(
                            loanTransfer: [
                                new LoanTransfer(
                                    accountNumber: '621585',
                                    amount: 500.00,
                                    day1: 15,
                                    effectiveDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2021-02-02').toString()),
                                    expirationDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-02').toString()),
                                    frequency: 4,
                                    id: '00002',
                                    idType: 1,
                                    locator: 1,
                                    lastDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-15').toString()),
                                    nextDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-03-15').toString()),
                                    type: 3
                                )
                            ]
                        )
                    )
                ]
            ),
            externalLoanList: new ExternalLoanList(
                externalLoan: [
                    new ExternalLoan(
                        accountNumber: '1234567890',
                        description: 'Fake external loan account',
                        closeDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                        externalLoanTransferList: new ExternalLoanTransferList(
                            externalLoanTransfer: [
                                new ExternalLoanTransfer(
                                    accountNumber: '621585',
                                    amount: 500.00,
                                    day1: 15,
                                    effectiveDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2021-02-02').toString()),
                                    expirationDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-02').toString()),
                                    frequency: 4,
                                    id: '00003',
                                    idType: 3,
                                    locator: 1,
                                    lastDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-15').toString()),
                                    nextDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-03-15').toString()),
                                    type: 3
                                )
                            ]
                        )
                    )
                ]
            )
        )
    )

    static ShareResponse shareResponse = new ShareResponse(
        messageId: 'Test',
        share: new Share(
            id: '00001',
            description: 'Fake share account',
            type: 0,
            balance: 120.00,
            availableBalance: 100.00,
            micrAcctNumber: '01',
            openDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
            regDCheckCount: 1,
            regDTransferCount: 1,
            divRate: 2.00,
            maturityDate: null,
            shareTransferList: new ShareTransferList(
                shareTransfer: [
                    new ShareTransfer(
                        accountNumber: '621585',
                        amount: 50.00,
                        day1: 15,
                        effectiveDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2021-02-02').toString()),
                        expirationDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-02').toString()),
                        frequency: 4,
                        id: '00001',
                        idType: 0,
                        locator: 1,
                        lastDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-15').toString()),
                        nextDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-03-15').toString()),
                        type: 3
                    )
                ]
            )
        )
    )

    static LoanResponse loanResponse = new LoanResponse(
        messageId: 'Test',
        loan: new Loan(
            id: '00002',
            description: 'Fake loan account',
            type: 1,
            balance: 1000.00,
            payment: 100.00,
            paymentDue: 100.00,
            dueDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
            lastPaymentDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
            openDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
            interestRate: 6.00,
            interestYtd: 50.00,
            creditLimit: null,
            availableCredit: null,
            micrAcctNumber: '02',
            maturityDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
            loanTransferList: new LoanTransferList(
                loanTransfer: [
                    new LoanTransfer(
                        accountNumber: '621585',
                        amount: 500.00,
                        day1: 15,
                        effectiveDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2021-02-02').toString()),
                        expirationDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-02').toString()),
                        frequency: 4,
                        id: '00002',
                        idType: 1,
                        locator: 1,
                        lastDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-02-15').toString()),
                        nextDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2022-03-15').toString()),
                        type: 3
                    )
                ]
            )
        )
    )

    static AccountSelectFieldsFilterChildrenResponse accountSelectFieldsFilterChildrenResponse_withNameRecords = new AccountSelectFieldsFilterChildrenResponse(
        messageId: 'Test',
        account: new Account(
            type: (short) 20,
            nameList: new NameList(
                name: [
                    new Name(
                        ssn: '555-55-5555',
                        birthDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                        homePhone: '555-555-5555',
                        mobilePhone: '444-444-4444',
                        workPhone: '333-333-3333',
                        email: 'user@email.com',
                        first: 'John',
                        last: 'Doe',
                        userChar2: 'test',
                        type: 0

                    )
                ]
            )
        )
    )

    static ShareTransactionSearchPagedSelectFieldsResponse shareTransactionSearchPagedSelectFieldsResponse = new ShareTransactionSearchPagedSelectFieldsResponse(
        messageId: 'Test',
        token: 'Test',
        shareTransaction: [
            new ShareTransaction(
                id: '11111-11111',
                accountNumber: '123456789',
                activityDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                description: 'Fake transaction record',
                tranAmount: 10.00
            )
        ]
    )

    static ShareHoldSearchPagedSelectFieldsResponse shareHoldSearchPagedSelectFieldsResponse = new ShareHoldSearchPagedSelectFieldsResponse(
        messageId: 'Test',
        token: null,
        shareHold: [
            new ShareHold(
                locator: 45,
                amount: 50.00,
                achRecurringStop: 0,
                availableBalance: 100.00,
                feeCode: 1,
                feeDescription: 'Fake share hold transaction record',
                payeeName: 'John Doe',
                stopPayCode: 23,
                holdCreationDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-03-02').toString()),
                effectiveDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-03-02').toString()),
                expirationDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-04-02').toString()),
                reference1: '000223344',
                reference2: '000443322'
            )
        ]
    )

    static LoanTransactionSearchPagedSelectFieldsResponse loanTransactionSearchPagedSelectFieldsResponse = new LoanTransactionSearchPagedSelectFieldsResponse(
        messageId: 'Test',
        token: 'Test',
        loanTransaction: [
            new LoanTransaction(
                id: '22222-22222',
                accountNumber: '123456789',
                activityDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString()),
                description: 'Fake loan transaction record',
                tranAmount: 100.00
            )
        ]
    )

    static LoanHoldSearchPagedSelectFieldsResponse loanHoldSearchPagedSelectFieldsResponse = new LoanHoldSearchPagedSelectFieldsResponse(
        messageId: 'Test',
        token: null,
        loanHold: [
            new LoanHold(
                amount: 33.00,
                availableBalance: 100.00,
                feeDescription: 'Fake loan hold transaction record',
                locator: 3,
                feeCode: 1,
                effectiveDate: DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-05-02').toString())
            )
        ]
    )

    static ShareUpdateByIDResponse updateShareByIDResponse = new ShareUpdateByIDResponse(
        messageId: 'Success',
        updateStatus: new UpdateStatus(
            isAllFieldsUpdateSuccess: true
        )
    )

    static LookupBySSNResponse lookupBySSNResponse = new LookupBySSNResponse(
        messageId: 'Test',
        accountNumber: [
            '12121212121',
            '32323232323'
        ]
    )

    static LookupByActiveCardResponse lookupByActiveCardResponse = new LookupByActiveCardResponse(
        messageId: 'Test',
        accountNumber: '12121212121'
    )

    static PowerOnExecutionResponse powerOnExecutionResponse = new PowerOnExecutionResponse(
        body: new ExecutionResponseBody(
            rgLines: """
                        {
                            "pendingtransactions": [
                                {
                                   "id": "101736126777107012008260000000000017043",
                                   "type": "04",
                                   "effectivedate": "09/01/2020",
                                   "amount": "00000000000436.24",
                                   "expirationdate": "09/01/2020",
                                   "payeename": "SSI  TREAS 310",
                                   "reference1": "ACH WHAREHOUSE",
                                   "reference2": "32"
                                },
                                {
                                   "id": "101736130128957012008270000000000030599",
                                   "type": "04",
                                   "effectivedate": "09/02/2020",
                                   "amount": "00000000001048.04",
                                   "expirationdate": "09/02/2020",
                                   "payeename": "SSI  TREAS 310",
                                   "reference1": "ACH WHAREHOUSE",
                                   "reference2": "32"
                                }
                            ]
                        }
                     """
        )
    )

    static ShareTransferCreateResponse shareTransferCreateResponse = new ShareTransferCreateResponse(
        messageId: 'Success',
        shareTransferLocator: 1
    )

    static ShareHoldCreateResponse shareHoldCreateResponse = new ShareHoldCreateResponse(
        messageId: 'Success',
        shareHoldLocator: 1
    )

    static LoanHoldCreateResponse loanHoldCreateResponse = new LoanHoldCreateResponse(
        messageId: 'Success',
        loanHoldLocator: 1
    )

    static TransactionsBaseResponse transactionsBaseResponse = new TransactionsBaseResponse(
        messageId: 'Success',
        confirmation: 'Withdrawal confirmed'
    )

    static ShareHoldUpdateByIDResponse shareHoldUpdateByIDResponse = new ShareHoldUpdateByIDResponse(
        messageId: 'Success',
        updateStatus: new UpdateStatus(
            isAllFieldsUpdateSuccess: true
        )
    )

    static TransactionsBaseResponse transactionsBaseResponse_withdrawalFeeFailure = new TransactionsBaseResponse(
        messageId: 'Failed',
        confirmation: null
    )

    static TransactionsOverdrawInformationResponse transactionsOverdrawInformationResponse = new TransactionsOverdrawInformationResponse(
        statusCode: 200,
        statusMessage: 'Success',
        confirmation: '1'
    )

    static InitiateTransferRequest initiateTransferRequest_scheduledTransfer = new InitiateTransferRequest(
        sourceAccountNumber: '000012345',
        sourceProductId: '0010',
        sourceProductType: ProductType.LOAN,
        destinationAccountNumber: '000054321',
        destinationProductId: '0020',
        amount: 10000,
        frequency: 1,
        day1: 12,
        destinationProductType: ProductType.SHARE
    )

    static InitiateTransferRequest initiateTransferRequest_immediateTransfer = new InitiateTransferRequest(
        sourceAccountNumber: '000012345',
        sourceProductId: '0010',
        sourceProductType: ProductType.LOAN,
        destinationAccountNumber: '000054321',
        destinationProductId: '0020',
        amount: 10000
    )

    static ShareTransferUpdateByIDResponse shareTransferUpdateByIDResponse = new ShareTransferUpdateByIDResponse(
        messageId: 'Test',
        updateStatus: new UpdateStatus(
            isAllFieldsUpdateSuccess: true
        )
    )

    static LoanTransferUpdateByIDResponse loanTransferUpdateByIDResponse = new LoanTransferUpdateByIDResponse(
        messageId: 'Test',
        updateStatus: new UpdateStatus(
            isAllFieldsUpdateSuccess: true
        )
    )

    static ExternalLoanTransferUpdateByIDResponse externalLoanTransferUpdateByIDResponse = new ExternalLoanTransferUpdateByIDResponse(
        messageId: 'Test',
        updateStatus: new UpdateStatus(
            isAllFieldsUpdateSuccess: true
        )
    )

    static UpdateNameRecordRequest updateNameRecordRequest = new UpdateNameRecordRequest(
        accountNumber: '000012323',
        workPhoneNumber: '111-11-1111',
        homePhoneNumber: '222-22-2222',
        mobilePhoneNumber: '333-33-3333',
        emailAddress: 'fake.user@email.com',
        streetAddress: '123 Main ST',
        city: 'Atlanta',
        state: 'GA',
        nameLocator: 123,
        preferredContactMethod: 3,
        zipCode: '36579',
    )

    static NameUpdateByIDResponse nameUpdateByIDResponse = new NameUpdateByIDResponse(
        messageId: 'Test',
        updateStatus: new UpdateStatus(
            isAllFieldsUpdateSuccess: true
        )
    )

    static NameDeleteResponse nameDeleteResponse = new NameDeleteResponse(
        messageId: 'Test'
    )

    static StopCheckPaymentRequest stopCheckPaymentRequest = new StopCheckPaymentRequest(
        type: 1,
        amount: 200.00,
        productId: '2352552',
        accountNumber: '000012324',
        effectiveDate: LocalDate.now(),
        feeCode: 3,
        feeAccountNumber: '000012324',
        feeProductId: '2352552',
        startingCheckNumber: '12345',
        endingCheckNumber: '12346',
        payeeName: 'John Doe',
        stopPayCode: StopPayCode.LOST
    )
}
