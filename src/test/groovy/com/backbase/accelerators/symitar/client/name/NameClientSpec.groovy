package com.backbase.accelerators.symitar.client.name

import com.backbase.accelerators.symitar.client.TestData
import com.backbase.accelerators.symitar.client.name.model.GetNameRecordsResponse
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.NameCreateResponse
import com.symitar.generated.symxchange.account.NameDeleteResponse
import com.symitar.generated.symxchange.account.NameUpdateByIDResponse
import com.symitar.generated.symxchange.account.dto.create.NameCreatableFields
import com.symitar.generated.symxchange.account.dto.retrieve.Name
import com.symitar.generated.symxchange.account.dto.update.NameUpdateableFields
import spock.lang.Specification

import javax.xml.datatype.DatatypeFactory
import java.time.LocalDate

class NameClientSpec extends Specification {

    AccountService accountService = Mock()
    NameClient nameClient = new NameClient(accountService, TestData.symitarRequestSettings)

    void 'getNameRecords returns a list of name records associated with a given account number'() {
        given: 'A memberId with an external loan filter'
        String memberId = '621585'
        String nameFilter = ''

        when: 'The nameClient is invoked'
        GetNameRecordsResponse result = nameClient.getNameRecords(memberId, nameFilter)

        then: 'The account service mock calls getAccountSelectFieldsFilterChildren exactly 1 time'
        1 * accountService.getAccountSelectFieldsFilterChildren(_) >> TestData.accountSelectFieldsFilterChildrenResponse_withNameRecords

        and: 'The expected results are returned'
        verifyNameRecords(result.nameRecords)
    }

    void 'createNameRecord adds a new name record in the core'() {
        given: 'An account number and nameCreatableFields object'
        String accountNumber = '518907'
        NameCreatableFields nameCreatableFields = TestData.nameCreatableFields

        when: 'The nameClient is invoked'
        NameCreateResponse result =  nameClient.createNameRecord(accountNumber, nameCreatableFields)

        then: 'The account service mock calls createName exactly 1 time'
        1 * accountService.createName(_) >> TestData.nameCreateResponse

        and: 'The expected results are returned'
        result.nameLocator == 137
    }

    void 'updateNameRecord adds/updates attributes of a name record'() {
        given: 'An account number, name locator and nameUpdateableFields object'
        String accountNumber = '518907'
        int nameLocator = 78
        NameUpdateableFields nameUpdateableFields = TestData.nameUpdateableFields

        when: 'The nameClient is invoked'
        NameUpdateByIDResponse result =  nameClient.updateNameRecord(accountNumber, nameLocator, nameUpdateableFields)

        then: 'The account service mock calls updateNameByID exactly 1 time'
        1 * accountService.updateNameByID(_) >> TestData.nameUpdateByIDResponse

        and: 'The expected results are returned'
        result.updateStatus.isAllFieldsUpdateSuccess
    }

    void 'deleteNameRecord deletes a name record from the core'() {
        given: 'An accountNumber and nameLocator'
        String accountNumber = '518907'
        Integer nameLocator = 137

        when: 'The nameClient is invoked'
        NameDeleteResponse result =  nameClient.deleteNameRecord(accountNumber, nameLocator)

        then: 'The account service mock calls deleteName exactly 1 time'
        1 * accountService.deleteName(_) >> TestData.nameDeleteResponse

        and: 'The expected results are returned'
        result.messageId == 'Test'
    }

    private void verifyNameRecords(List<Name> nameRecords) {
        verifyAll(nameRecords) {
            it[0].ssn == '555-55-5555'
            it[0].birthDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            it[0].homePhone == '555-555-5555'
            it[0].mobilePhone == '444-444-4444'
            it[0].workPhone == '333-333-3333'
            it[0].email == 'user@email.com'
            it[0].first == 'John'
            it[0].last == 'Doe'
            it[0].userChar2 == 'test'
            it[0].type == 0
        }
    }
}
