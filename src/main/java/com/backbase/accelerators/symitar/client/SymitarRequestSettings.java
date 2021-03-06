package com.backbase.accelerators.symitar.client;

import com.symitar.generated.symxchange.common.dto.common.AdminCredentialsChoice;
import com.symitar.generated.symxchange.common.dto.common.CredentialsChoice;
import com.symitar.generated.symxchange.common.dto.common.DeviceInformation;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SymitarRequestSettings {

    private String baseUrl;
    private String messageId;
    private DeviceInformation deviceInformation;
    private CredentialsChoice credentialsChoice;
    private AdminCredentialsChoice adminCredentialsChoice;
    private StopCheckPaymentSettings stopCheckPaymentSettings;
    private WireTransferSettings wireTransferSettings;

    @Data
    public static class StopCheckPaymentSettings {

        private BigDecimal withdrawalFeeAmount;
        private String withdrawalFeeReasonText;
        private short withdrawalFeeCode;
        private short generalLedgerClearingCode;
    }

    @Data
    public static class WireTransferSettings {

        private BigDecimal withdrawalFeeAmount;
        private String withdrawalFeeReasonText;
        private String sourceCode;
        private short withdrawalFeeCode;
        private short generalLedgerClearingCode;
    }
}
