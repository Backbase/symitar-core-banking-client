package com.backbase.accelerators.symitar.client.name

import com.backbase.accelerators.symitar.client.TestData
import com.backbase.accelerators.symitar.client.name.model.GetNameRecordsResponse
import com.backbase.accelerators.symitar.client.name.model.UpdateNameRecordRequest
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.NameUpdateByIDResponse
import com.symitar.generated.symxchange.account.dto.retrieve.Name
import com.symitar.generated.symxchange.account.dto.retrieve.NameList
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

        when: 'The account client is invoked'
        GetNameRecordsResponse result = nameClient.getNameRecords(memberId, nameFilter)

        then: 'The account service mock calls getAccountSelectFieldsFilterChildren exactly 1 time'
        1 * accountService.getAccountSelectFieldsFilterChildren(_) >> TestData.accountSelectFieldsFilterChildrenResponse_withNameRecords

        and: 'The expected results are returned'
        verifyMemberProfile(result.nameRecords)
    }

    void 'updateMemberProfile adds/updates attributes of a member profile'() {
        given: 'An updateMemberProfileRequest'
        UpdateNameRecordRequest updateNameRecordRequest = TestData.updateNameRecordRequest

        when: 'The account client is invoked'
        NameUpdateByIDResponse result =  nameClient.updateNameRecord(updateNameRecordRequest)

        then: 'The account service mock calls updateNameByID exactly 1 time'
        1 * accountService.updateNameByID(_) >> TestData.nameUpdateByIDResponse

        and: 'The expected results are returned'
        result.updateStatus.isAllFieldsUpdateSuccess
    }

    private void verifyMemberProfile(NameList nameList) {
        verifyAll(nameList) {
            name[0].ssn == '555-55-5555'
            name[0].birthDate == DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse('2020-02-02').toString())
            name[0].homePhone == '555-555-5555'
            name[0].mobilePhone == '444-444-4444'
            name[0].workPhone == '333-333-3333'
            name[0].email == 'user@email.com'
            name[0].first == 'John'
            name[0].last == 'Doe'
            name[0].userChar2 == 'test'
            name[0].type == 0

        }
    }

    private void verifyMemberProfile(List<Name> nameRecords) {
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
