package com.backbase.accelerators.symitar.client.name.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateNameRecordRequest {

    private int nameLocator;
    private String accountNumber;
    private String workPhoneNumber;
    private String workPhoneNumberExtension;
    private String homePhoneNumber;
    private String mobilePhoneNumber;
    private String emailAddress;
    private String alternateEmailAddress;
    private String streetAddress;
    private String streetAddressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String countryCode;
    private short type;
    private short addressType;
    private short preferredContactMethod;
    private LocalDate namedRecordExpirationDate;
}
