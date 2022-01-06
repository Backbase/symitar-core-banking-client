package com.backbase.accelerators.symitar.client.findby;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.backbase.accelerators.symitar.client.util.SymitarUtils;
import com.symitar.generated.symxchange.findby.FindByService;
import com.symitar.generated.symxchange.findby.dto.LookupBySSNRequest;
import com.symitar.generated.symxchange.findby.dto.LookupBySSNResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class FindByClient {

    private final SymitarRequestSettings symitarRequestSettings;
    private final FindByService findByService;

    public FindByClient(SymitarRequestSettings symitarRequestSettings, FindByService findByService) {
        Objects.requireNonNull(
            findByService,
            "FindByClient cannot be initialized because FindByService is null.");

        Objects.requireNonNull(
            symitarRequestSettings,
            "FindByClient cannot be initialized because SymitarRequestSettings is null.");

        this.symitarRequestSettings = symitarRequestSettings;
        this.findByService = findByService;
    }

    /**
     * Returns a list of member account numbers associated with the given social security number.
     * @param ssn the social security number of the user
     * @return a list of member account numbers
     */
    public LookupBySSNResponse findBySsn(String ssn) {
        LookupBySSNRequest lookupBySsnRequest = new LookupBySSNRequest();
        lookupBySsnRequest.setCredentials(symitarRequestSettings.getAdminCredentialsChoice());
        lookupBySsnRequest.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        lookupBySsnRequest.setMessageId(symitarRequestSettings.getMessageId());
        lookupBySsnRequest.setSSN(ssn);

        log.debug("Invoking findBySsn with request: {}", SymitarUtils.toXmlString(lookupBySsnRequest));
        return findByService.findBySSN(lookupBySsnRequest);
    }
}
