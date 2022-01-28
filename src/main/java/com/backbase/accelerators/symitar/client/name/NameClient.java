package com.backbase.accelerators.symitar.client.name;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.name.model.CreateNameRecordRequest;
import com.backbase.accelerators.symitar.client.name.model.GetNameRecordsResponse;
import com.backbase.accelerators.symitar.client.name.model.UpdateNameRecordRequest;
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

import static com.backbase.accelerators.symitar.client.util.SymitarUtils.DateType.NAME_UPDATABLE_FIELDS_EXPIRATION_DATE;

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
     * @param request a CreateNameRecordRequest
     * @return a NameCreateResponse containing the name locator of the newly-created record
     */
    public NameCreateResponse createNameRecord(CreateNameRecordRequest request) {

        NameCreatableFields nameCreatableFields = new NameCreatableFields();
        nameCreatableFields.setWorkPhone(request.getWorkPhoneNumber());
        nameCreatableFields.setWorkPhoneExtension(request.getWorkPhoneNumberExtension());
        nameCreatableFields.setHomePhone(request.getHomePhoneNumber());
        nameCreatableFields.setMobilePhone(request.getMobilePhoneNumber());
        nameCreatableFields.setEmail(request.getEmailAddress());
        nameCreatableFields.setAltEmail(request.getAlternateEmailAddress());
        nameCreatableFields.setStreet(truncate(request.getStreetAddress(), 40));
        nameCreatableFields.setExtraAddress(truncate(request.getStreetAddressLine2(), 40));
        nameCreatableFields.setCity(truncate(request.getCity(), 40));
        nameCreatableFields.setState(truncate(request.getState(), 10));
        nameCreatableFields.setZipCode(truncate(request.getZipCode(), 10));
        nameCreatableFields.setCountry(request.getCountry());
        nameCreatableFields.setCountryCode(request.getCountryCode());
        nameCreatableFields.setType(request.getType());
        nameCreatableFields.setAddressType(request.getAddressType());
        nameCreatableFields.setPreferredContactMethod(request.getPreferredContactMethod());

        nameCreatableFields.setExpirationDate(SymitarUtils.convertToXmlGregorianCalendar(
            request.getNamedRecordExpirationDate(),
            NAME_UPDATABLE_FIELDS_EXPIRATION_DATE));

        CreateNameRequest createNameRequest = new CreateNameRequest();
        createNameRequest.setAccountNumber(request.getAccountNumber());
        createNameRequest.setMessageId(symitarRequestSettings.getMessageId());
        createNameRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        createNameRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        createNameRequest.setNameCreatableFields(nameCreatableFields);

        log.debug("Invoking createName with request: {}", SymitarUtils.toXmlString(createNameRequest));
        return accountService.createName(createNameRequest);
    }

    /**
     * Adds, updates and deletes properties on a name record for an account. Properties marked for deletion should be
     * set to empty strings.
     *
     * @param request contains the name record properties to update.
     * @return
     */
    public NameUpdateByIDResponse updateNameRecord(UpdateNameRecordRequest request) {

        NameUpdateableFields nameUpdateableFields = new NameUpdateableFields();
        nameUpdateableFields.setWorkPhone(request.getWorkPhoneNumber());
        nameUpdateableFields.setWorkPhoneExtension(request.getWorkPhoneNumberExtension());
        nameUpdateableFields.setHomePhone(request.getHomePhoneNumber());
        nameUpdateableFields.setMobilePhone(request.getMobilePhoneNumber());
        nameUpdateableFields.setEmail(request.getEmailAddress());
        nameUpdateableFields.setAltEmail(request.getAlternateEmailAddress());
        nameUpdateableFields.setStreet(truncate(request.getStreetAddress(), 40));
        nameUpdateableFields.setExtraAddress(truncate(request.getStreetAddressLine2(), 40));
        nameUpdateableFields.setCity(truncate(request.getCity(), 40));
        nameUpdateableFields.setState(truncate(request.getState(), 10));
        nameUpdateableFields.setZipCode(truncate(request.getZipCode(), 10));
        nameUpdateableFields.setCountry(request.getCountry());
        nameUpdateableFields.setCountryCode(request.getCountryCode());
        nameUpdateableFields.setType(request.getType());
        nameUpdateableFields.setAddressType(request.getAddressType());
        nameUpdateableFields.setPreferredContactMethod(request.getPreferredContactMethod());

        nameUpdateableFields.setExpirationDate(SymitarUtils.convertToXmlGregorianCalendar(
            request.getNamedRecordExpirationDate(),
            NAME_UPDATABLE_FIELDS_EXPIRATION_DATE));

        UpdateNameByIDRequest updateNameByIdRequest = new UpdateNameByIDRequest();
        updateNameByIdRequest.setAccountNumber(request.getAccountNumber());
        updateNameByIdRequest.setMessageId(symitarRequestSettings.getMessageId());
        updateNameByIdRequest.setCredentials(symitarRequestSettings.getCredentialsChoice());
        updateNameByIdRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        updateNameByIdRequest.setNameLocator(request.getNameLocator());
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

    /**
     * Truncates the provided text to the given length to align the length requirements of the core.
     *
     * @param text   the text to truncate
     * @param length the desired length of the truncated text
     * @return a truncated string
     */
    private String truncate(String text, int length) {
        return StringUtils.substring(text, 0, length);
    }
}
