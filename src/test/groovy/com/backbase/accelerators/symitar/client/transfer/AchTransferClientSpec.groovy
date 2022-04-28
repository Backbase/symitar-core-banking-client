package com.backbase.accelerators.symitar.client.transfer

import com.backbase.accelerators.symitar.client.TestData
import com.symitar.generated.symxchange.account.AccountService
import com.symitar.generated.symxchange.account.EftCreateResponse
import com.symitar.generated.symxchange.account.EftUpdateByIDResponse
import spock.lang.Specification

class AchTransferClientSpec extends Specification {

    private AccountService accountService = Mock()
    private AchTransferClient transferClient = new AchTransferClient(accountService, TestData.symitarRequestSettings)

    void 'createAchTransfer creates an ACH transfer in the core' () {
        given: 'an account number'
        String accountNumber = '000123453'

        when:
        EftCreateResponse result = transferClient.createAchTransfer(accountNumber, TestData.eftCreatableFields)

        then:
        1 * accountService.createEft(_) >> TestData.eftCreateResponse

        and:
        result.eftLocator == 5453
    }

    void 'updateAchTransfer updates an ACH transfer in the core' () {
        given: 'an account number'
        String accountNumber = '000123453'
        int locator = 5453

        when:
        EftUpdateByIDResponse result = transferClient.updateAchTransfer(accountNumber, locator, TestData.eftCreatableFields)

        then:
        1 * accountService.updateEftByID(_) >> TestData.eftUpdateByIDResponse

        and:
        result.updateStatus.isAllFieldsUpdateSuccess
    }

}
