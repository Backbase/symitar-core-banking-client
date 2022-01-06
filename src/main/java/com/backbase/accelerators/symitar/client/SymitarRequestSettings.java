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
    private BigDecimal stopCheckPaymentWithdrawalFeeAmount;
    private Integer stopCheckPaymentWithdrawalFeeCode;
    private String stopCheckPaymentWithdrawalFeeReasonText;
}
