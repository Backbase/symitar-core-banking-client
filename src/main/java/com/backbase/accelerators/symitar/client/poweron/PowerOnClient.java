package com.backbase.accelerators.symitar.client.poweron;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.poweron.model.AchPendingTransactions;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class PowerOnClient {

    private static final String PENDING_TRANSACTIONS = "pendingtransactions";
    private static final String MESSAGE_ID = "executePowerOn";
    private static final String RG_STATE = "START";
    private static final String FILE = "OLB.ACH.PENDINGTXNS";

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

    private ExecutionHeader createExecutionHeader() {
        ExecutionHeader executionHeader = new ExecutionHeader();
        executionHeader.setMessageID(MESSAGE_ID);
        executionHeader.setRGState(RG_STATE);

        return executionHeader;
    }

    private ExecutionRequestBody createExecutionRequestBody(String productId, String accountNumber) {
        ExecutionRequestBody executionRequestBody = new ExecutionRequestBody();
        executionRequestBody.setFile(FILE);
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

        return objectMapper.readValue(jsonNode.toString(), new TypeReference<>() {});
    }
}
