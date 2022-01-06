package com.backbase.accelerators.symitar.client.poweron

import com.backbase.accelerators.symitar.client.TestData
import com.backbase.accelerators.symitar.client.poweron.model.AchPendingTransactions
import com.symitar.generated.symxchange.poweron.PowerOnService
import com.symitar.generated.symxchange.poweron.dto.PowerOnExecutionRequest
import spock.lang.Specification

class PowerOnClientSpec extends Specification {

    private PowerOnService powerOnService = Mock()
    private PowerOnClient powerOnClient = new PowerOnClient(powerOnService, TestData.symitarRequestSettings)

    void 'getACHPendingTransactions returns a list of pending ach transactions for the provided product id and account number'() {
        given: 'A product Id and account number'
        String productId = '1243534'
        String accountNumber = "000034342"

        when: 'powerOnClient is invoked'
        List<AchPendingTransactions> result = powerOnClient.getAchPendingTransactions(productId, accountNumber)

        then: 'The power on service mock calls executePowerOn exactly 1 time'
        1 * powerOnService.executePowerOn(_ as PowerOnExecutionRequest) >> TestData.powerOnExecutionResponse

        and: 'The expected results are returned'
        verifyAll(result) {
            it[0].id == '101736126777107012008260000000000017043'
            it[0].type == '04'
            it[0].effectiveDate == '09/01/2020'
            it[0].amount == '00000000000436.24'
            it[0].expirationDate == '09/01/2020'
            it[0].payeeName == 'SSI  TREAS 310'
            it[0].reference1 == 'ACH WHAREHOUSE'
            it[0].reference2 == '32'

            it[1].id == '101736130128957012008270000000000030599'
            it[1].type == '04'
            it[1].effectiveDate == '09/02/2020'
            it[1].amount == '00000000001048.04'
            it[1].expirationDate == '09/02/2020'
            it[1].payeeName == 'SSI  TREAS 310'
            it[1].reference1 == 'ACH WHAREHOUSE'
            it[1].reference2 == '32'
        }
    }
}
