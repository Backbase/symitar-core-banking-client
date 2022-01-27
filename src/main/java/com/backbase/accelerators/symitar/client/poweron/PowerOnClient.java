package com.backbase.accelerators.symitar.client.poweron;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.poweron.model.AchPendingTransactions;
import com.backbase.accelerators.symitar.client.poweron.model.LoanPayoffResult;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symitar.generated.symxchange.poweron.PowerOnService;
import com.symitar.generated.symxchange.poweron.dto.ExecutionHeader;
import com.symitar.generated.symxchange.poweron.dto.ExecutionRequestBody;
import com.symitar.generated.symxchange.poweron.dto.PowerOnExecutionRequest;
import com.symitar.generated.symxchange.poweron.dto.PowerOnExecutionResponse;
import com.symitar.generated.symxchange.poweron.dto.UserChr;
import com.symitar.generated.symxchange.poweron.dto.UserDefinedParameters;
import com.symitar.generated.symxchange.poweron.dto.UserNum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
public class PowerOnClient {

    private static final String PENDING_TRANSACTIONS = "pendingtransactions";
    private static final String MESSAGE_ID = "executePowerOn";
    private static final String RG_STATE = "START";
    private static final String ACH_PENDING_TRANSACTIONS_FILE = "OLB.ACH.PENDINGTXNS";
    private static final String BALANCE_TRANSFER_FILE = "SYC.CHECK.ADV.CC.BT";
    private static final String LOAN_PAYOFF_FILE = "LOANPAYOFF.JSON";

    private final PowerOnService powerOnService;
    private final SymitarRequestSettings symitarRequestSettings;

    public PowerOnClient(PowerOnService powerOnService, SymitarRequestSettings symitarRequestSettings) {
        Objects.requireNonNull(
            powerOnService,
            "PowerOnClient cannot be initialized because PowerOnService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "PowerOnClient cannot be initialized because SymitarRequestSettings is null.");

        this.powerOnService = powerOnService;
        this.symitarRequestSettings = symitarRequestSettings;
    }

    public List<AchPendingTransactions> getAchPendingTransactions(String productId, String accountNumber) {
        PowerOnExecutionRequest powerOnExecutionRequest = new PowerOnExecutionRequest();
        powerOnExecutionRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        powerOnExecutionRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        powerOnExecutionRequest.setHeader(createExecutionHeader());
        powerOnExecutionRequest.setBody(createExecutionRequestBody(productId, accountNumber));

        log.debug("Invoking executePowerOn() with request: {}", SymitarUtils.toXmlString(powerOnExecutionRequest));
        PowerOnExecutionResponse powerOnExecutionResponse = powerOnService.executePowerOn(powerOnExecutionRequest);
        log.debug("PowerOnExecutionResponse: {}", SymitarUtils.toXmlString(powerOnExecutionResponse));

        String responseJson = powerOnExecutionResponse.getBody().getRGLines();
        return toAchPendingTransactions(responseJson);
    }

    public PowerOnExecutionResponse postBalanceTransferPayeeRepGen(
        BigDecimal amount,
        String loanRepGenUserNum,
        String shareRepGenUserNum) {

        PowerOnExecutionRequest powerOnExecutionRequest = new PowerOnExecutionRequest();
        powerOnExecutionRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        powerOnExecutionRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        powerOnExecutionRequest.getHeader().setMessageID(symitarRequestSettings.getMessageId());
        powerOnExecutionRequest.setHeader(new ExecutionHeader());
        powerOnExecutionRequest.setBody(new ExecutionRequestBody());
        powerOnExecutionRequest.getBody().setFile(BALANCE_TRANSFER_FILE);
        powerOnExecutionRequest.getBody().setRGSession(1);

        UserDefinedParameters userDefinedParameters = new UserDefinedParameters();
        UserNum userNum;

        if (Objects.nonNull(loanRepGenUserNum)) {
            userNum = new UserNum();
            userNum.setID((short) 1);
            userNum.setValue(Integer.parseInt(loanRepGenUserNum));
            userDefinedParameters.getRGUserNum().add(userNum);
        }

        userNum = new UserNum();
        userNum.setID((short) 2);
        userNum.setValue(amount.intValue());

        userDefinedParameters.getRGUserNum().add(userNum);

        if (Objects.nonNull(shareRepGenUserNum)) {
            userNum = new UserNum();
            userNum.setID((short) 3);
            userNum.setValue(Integer.parseInt(shareRepGenUserNum));
            userDefinedParameters.getRGUserNum().add(userNum);
        }

        powerOnExecutionRequest.getBody().setUserDefinedParameters(userDefinedParameters);

        log.debug("Invoking executePowerOn() with request: {}", SymitarUtils.toXmlString(powerOnExecutionRequest));
        return powerOnService.executePowerOn(powerOnExecutionRequest);
    }

    public LoanPayoffResult getLoanPayOff(String id, String date) {
        PowerOnExecutionRequest powerOnExecutionRequest = new PowerOnExecutionRequest();
        powerOnExecutionRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        powerOnExecutionRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        powerOnExecutionRequest.getHeader().setMessageID(symitarRequestSettings.getMessageId());
        powerOnExecutionRequest.setHeader(new ExecutionHeader());
        powerOnExecutionRequest.setBody(new ExecutionRequestBody());
        powerOnExecutionRequest.getBody().setFile(LOAN_PAYOFF_FILE);
        powerOnExecutionRequest.getBody().setRGSession(1);

        UserChr idUsrChar = new UserChr();
        idUsrChar.setID((short) 1);
        idUsrChar.setValue(id);

        UserChr dateUserChr = new UserChr();
        dateUserChr.setID((short) 2);
        dateUserChr.setValue(date);

        UserDefinedParameters userDefinedParameters = new UserDefinedParameters();
        userDefinedParameters.getRGUserChr().add(idUsrChar);
        userDefinedParameters.getRGUserChr().add(dateUserChr);

        powerOnExecutionRequest.getBody().setUserDefinedParameters(userDefinedParameters);

        log.debug("Invoking executePowerOn() with request: {}", SymitarUtils.toXmlString(powerOnExecutionRequest));
        PowerOnExecutionResponse response = powerOnService.executePowerOn(powerOnExecutionRequest);
        String rgLines = response.getBody().getRGLines();
        return toLoanPayoffResult(rgLines);
    }

    private ExecutionHeader createExecutionHeader() {
        ExecutionHeader executionHeader = new ExecutionHeader();
        executionHeader.setMessageID(MESSAGE_ID);
        executionHeader.setRGState(RG_STATE);

        return executionHeader;
    }

    private ExecutionRequestBody createExecutionRequestBody(String productId, String accountNumber) {
        ExecutionRequestBody executionRequestBody = new ExecutionRequestBody();
        executionRequestBody.setFile(ACH_PENDING_TRANSACTIONS_FILE);
        executionRequestBody.setRGSession(1);
        executionRequestBody.setUserDefinedParameters(createUserDefinedParameters(productId, accountNumber));

        return executionRequestBody;
    }

    private UserDefinedParameters createUserDefinedParameters(String productId, String accountNumber) {
        UserChr userChr = new UserChr();
        userChr.setID((short) 1);
        userChr.setValue(accountNumber);

        UserChr userChr2 = new UserChr();
        userChr2.setID((short) 2);
        userChr2.setValue(productId);

        UserDefinedParameters userDefinedParameters = new UserDefinedParameters();
        userDefinedParameters.getRGUserChr().add(userChr);
        userDefinedParameters.getRGUserChr().add(userChr2);

        return userDefinedParameters;
    }

    @SneakyThrows
    private List<AchPendingTransactions> toAchPendingTransactions(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class)
            .get(PENDING_TRANSACTIONS);

        return objectMapper.readValue(jsonNode.toString(), new TypeReference<>() {
        });
    }

    @SneakyThrows
    private LoanPayoffResult toLoanPayoffResult(String json) {
        return new ObjectMapper().readValue(json, LoanPayoffResult.class);
    }
}
