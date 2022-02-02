package com.backbase.accelerators.symitar.client.name;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.name.model.GetNameRecordsResponse;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenRequest;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenResponse;
import com.symitar.generated.symxchange.account.AccountService;
import com.symitar.generated.symxchange.account.CreateNameRequest;
import com.symitar.generated.symxchange.account.DeleteNameRequest;
import com.symitar.generated.symxchange.account.NameCreateResponse;
import com.symitar.generated.symxchange.account.NameDeleteResponse;
import com.symitar.generated.symxchange.account.NameUpdateByIDResponse;
import com.symitar.generated.symxchange.account.UpdateNameByIDRequest;
import com.symitar.generated.symxchange.account.dto.create.NameCreatableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.Account;
import com.symitar.generated.symxchange.account.dto.retrieve.Name;
import com.symitar.generated.symxchange.account.dto.retrieve.NameFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.NameList;
import com.symitar.generated.symxchange.account.dto.retrieve.NameSelectableFields;
import com.symitar.generated.symxchange.account.dto.update.NameUpdateableFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class NameClient {

    private final SymitarRequestSettings symitarRequestSettings;
    private final AccountService accountService;

    public NameClient(AccountService accountService, SymitarRequestSettings symitarRequestSettings) {
        Objects.requireNonNull(
            accountService,
            "NameClient cannot be initialized because AccountService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "NameClient cannot be initialized because SymitarRequestSettings is null.");

        this.accountService = accountService;
        this.symitarRequestSettings = symitarRequestSettings;
    }

    /**
     * Returns a list of name records for the provided account number. Name records represent profiles of
     * person and non-person entities associated with the account.
     *
     * @param accountNumber the member account number
     * @param nameFilter an optional query string for filtering the returned results
     * @return A list of name records
     */
    public GetNameRecordsResponse getNameRecords(String accountNumber, String nameFilter) {

        AccountSelectFieldsFilterChildrenRequest request =
            SymitarUtils.initializeAccountSelectFieldsFilterChildrenRequest(symitarRequestSettings, accountNumber);

        if (StringUtils.isNotBlank(nameFilter)) {
            setNameFilterQuery(nameFilter, request);
        }

        NameSelectableFields nameSelectableFields = new NameSelectableFields();
        nameSelectableFields.setIncludeAllNameFields(true);
        request.getSelectableFields().setNameSelectableFields(nameSelectableFields);

        log.debug("Invoking getAccountSelectFieldsFilterChildren with request: {}", SymitarUtils.toXmlString(request));
        return mapToGetNameRecordsResponse(accountService.getAccountSelectFieldsFilterChildren(request));
    }

    /**
     * Creates a new name record in the core.
     * @param accountNumber the member account number
     * @param nameCreatableFields contains the properties that will be used to create the name record
     * @return
     */
    public NameCreateResponse createNameRecord(String accountNumber, NameCreatableFields nameCreatableFields) {

        CreateNameRequest createNameRequest = new CreateNameRequest();
        createNameRequest.setAccountNumber(accountNumber);
        createNameRequest.setMessageId(symitarRequestSettings.getMessageId());
        createNameRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        createNameRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        createNameRequest.setNameCreatableFields(nameCreatableFields);

        log.debug("Invoking createName with request: {}", SymitarUtils.toXmlString(createNameRequest));
        return accountService.createName(createNameRequest);
    }

    /**
     * Updates a name record in the core.
     * @param accountNumber the member account number
     * @param nameLocator the unique identifier of the name record
     * @param nameUpdateableFields contains the properties that will be used to update the name record
     * @return
     */
    public NameUpdateByIDResponse updateNameRecord(
        String accountNumber,
        int nameLocator,
        NameUpdateableFields nameUpdateableFields) {

        UpdateNameByIDRequest updateNameByIdRequest = new UpdateNameByIDRequest();
        updateNameByIdRequest.setMessageId(symitarRequestSettings.getMessageId());
        updateNameByIdRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        updateNameByIdRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        updateNameByIdRequest.setAccountNumber(accountNumber);
        updateNameByIdRequest.setNameLocator(nameLocator);
        updateNameByIdRequest.setNameUpdateableFields(nameUpdateableFields);

        log.debug("Invoking updateNameByID with request: {}", SymitarUtils.toXmlString(updateNameByIdRequest));
        return accountService.updateNameByID(updateNameByIdRequest);
    }

    public NameDeleteResponse deleteNameRecord(String accountNumber, Integer nameLocator) {

        DeleteNameRequest deleteNameRequest = new DeleteNameRequest();
        deleteNameRequest.setNameLocator(nameLocator);
        deleteNameRequest.setAccountNumber(accountNumber);
        deleteNameRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        deleteNameRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        deleteNameRequest.setMessageId(symitarRequestSettings.getMessageId());

        log.debug("Invoking deleteName with request: {}", SymitarUtils.toXmlString(deleteNameRequest));
        return accountService.deleteName(deleteNameRequest);
    }

    private void setNameFilterQuery(String nameFilter, AccountSelectFieldsFilterChildrenRequest request) {
        NameFilter nf = new NameFilter();
        nf.setQuery(nameFilter);

        request.getChildrenSearchFilter().setNameFilter(nf);
    }

    private GetNameRecordsResponse mapToGetNameRecordsResponse(AccountSelectFieldsFilterChildrenResponse response) {
        Optional<Account> account = Optional.ofNullable(response.getAccount());

        Short accountType = account.map(Account::getType)
            .orElse(null);

        List<Name> nameRecords = account.map(Account::getNameList)
            .map(NameList::getName)
            .orElse(Collections.emptyList());

        GetNameRecordsResponse getNameRecordsResponse = new GetNameRecordsResponse();
        getNameRecordsResponse.setAccountType(accountType);
        getNameRecordsResponse.setNameRecords(nameRecords);

        return getNameRecordsResponse;
    }
}
