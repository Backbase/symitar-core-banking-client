package com.backbase.accelerators.symitar.client.findby

import com.backbase.accelerators.symitar.client.TestData
import com.symitar.generated.symxchange.findby.FindByService
import com.symitar.generated.symxchange.findby.dto.LookupBySSNRequest
import com.symitar.generated.symxchange.findby.dto.LookupBySSNResponse
import spock.lang.Specification

class FindByClientSpec extends Specification {

    private FindByService findByService = Mock()
    private FindByClient findByClient = new FindByClient(TestData.symitarRequestSettings, findByService)

    void 'findBySSN returns a list of account numbers for the provided ssn'() {
        given: 'A LookupBySSNRequest'

        when: 'findByService is invoked'
        LookupBySSNResponse result = findByClient.findBySsn('555-55-555')

        then: 'The find by service mock calls findBySSN exactly 1 time'
        1 * findByService.findBySSN(_ as LookupBySSNRequest) >> TestData.lookupBySSNResponse

        and: 'The expected results are verified'
        verifyAll(result) {
            result.accountNumber[0] == '12121212121'
            result.accountNumber[1] == '32323232323'
        }
    }

}
