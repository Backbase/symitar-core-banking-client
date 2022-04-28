package com.backbase.accelerators.symitar.client.util;

import com.backbase.accelerators.symitar.client.SymitarRequestSettings;
import com.symitar.generated.symxchange.account.AccountSelectFieldsFilterChildrenRequest;
import com.symitar.generated.symxchange.account.dto.retrieve.AccountChildrenFilter;
import com.symitar.generated.symxchange.account.dto.retrieve.AccountSelectableFields;
import com.symitar.generated.symxchange.account.dto.retrieve.CardSelectableFields;
import com.symitar.generated.symxchange.account.dto.update.ObjectFactory;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Objects;

@UtilityClass
public class SymitarUtils {

    public enum DateType {
        SHARE_HOLD_UPDATABLE_FIELDS_EFFECTIVE_DATE,
        SHARE_HOLD_UPDATABLE_FIELDS_EXPIRATION_DATE,
        LOAN_HOLD_UPDATABLE_FIELDS_EFFECTIVE_DATE,
        LOAN_HOLD_UPDATABLE_FIELDS_EXPIRATION_DATE,
        SHARE_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE,
        SHARE_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE,
        SHARE_TRANSFER_UPDATABLE_FIELDS_NEXT_DATE,
        LOAN_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE,
        LOAN_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE,
        EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE,
        EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE,
        EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_NEXT_DATE,
        NAME_UPDATABLE_FIELDS_EXPIRATION_DATE,
        EFT_UPDATABLE_FIELDS_EFFECTIVE_DATE,
        EFT_UPDATABLE_FIELDS_EXPIRATION_DATE
    }

    public String leftPadAccountNumber(String accountNumber) {
        if (StringUtils.isEmpty(accountNumber)) {
            return null;
        }

        return String.format("%010d", Integer.parseInt(accountNumber));
    }

    public String toString(Object object) {
        return new ReflectionToStringBuilder(object, new RecursiveToStringStyle()).toString();
    }

    public String toXmlString(Object obj) {
        StringWriter stringWriter = new StringWriter();
        JAXB.marshal(obj, stringWriter);
        return stringWriter.toString();
    }

    public LocalDate toLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (Objects.isNull(xmlGregorianCalendar)) {
            throw new IllegalArgumentException("xmlGregorianCalendar is null");
        }

        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    public AccountSelectFieldsFilterChildrenRequest initializeAccountSelectFieldsFilterChildrenRequest(
        SymitarRequestSettings symitarRequestSettings,
        String accountNumber) {

        AccountSelectFieldsFilterChildrenRequest request = new AccountSelectFieldsFilterChildrenRequest();
        request.setAccountNumber(accountNumber);
        request.setMessageId(symitarRequestSettings.getMessageId());
        request.setCredentials(symitarRequestSettings.getCredentialsChoice());
        request.setDeviceInformation(symitarRequestSettings.getDeviceInformation());
        request.setChildrenSearchFilter(new AccountChildrenFilter());

        CardSelectableFields cardSelectableFields = new CardSelectableFields();
        cardSelectableFields.setIncludeAllCardFields(true);

        AccountSelectableFields accountSelectableFields = new AccountSelectableFields();
        accountSelectableFields.setIncludeAllAccountFields(true);
        accountSelectableFields.setCardSelectableFields(cardSelectableFields);

        request.setSelectableFields(accountSelectableFields);
        return request;
    }

    @SneakyThrows
    public XMLGregorianCalendar convertToXmlGregorianCalendar(LocalDate localDate) {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
    }

    public JAXBElement<XMLGregorianCalendar> convertToXmlGregorianCalendar(LocalDate localDate, DateType dateType) {
        if (Objects.isNull(localDate)) {
            return null;
        }

        if (dateType == DateType.SHARE_HOLD_UPDATABLE_FIELDS_EFFECTIVE_DATE) {
            return new ObjectFactory().createShareHoldUpdateableFieldsEffectiveDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.SHARE_HOLD_UPDATABLE_FIELDS_EXPIRATION_DATE) {
            return new ObjectFactory().createShareHoldUpdateableFieldsExpirationDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.SHARE_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE) {
            return new ObjectFactory().createShareTransferUpdateableFieldsEffectiveDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.SHARE_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE) {
            return new ObjectFactory().createShareTransferUpdateableFieldsExpirationDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.SHARE_TRANSFER_UPDATABLE_FIELDS_NEXT_DATE) {
            return new ObjectFactory().createShareTransferUpdateableFieldsNextDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.LOAN_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE) {
            return new ObjectFactory().createLoanTransferUpdateableFieldsExpirationDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EXPIRATION_DATE) {
            return new ObjectFactory().createExternalLoanTransferUpdateableFieldsExpirationDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));
        } else if (dateType == DateType.LOAN_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE) {
            return new ObjectFactory().createLoanTransferUpdateableFieldsEffectiveDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));
        } else if (dateType == DateType.EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_EFFECTIVE_DATE) {
            return new ObjectFactory().createExternalLoanTransferUpdateableFieldsEffectiveDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));
        } else if (dateType == DateType.EXTERNAL_LOAN_TRANSFER_UPDATABLE_FIELDS_NEXT_DATE) {
            return new ObjectFactory().createExternalLoanTransferUpdateableFieldsNextDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.LOAN_HOLD_UPDATABLE_FIELDS_EFFECTIVE_DATE) {
            return new ObjectFactory().createShareHoldUpdateableFieldsEffectiveDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.LOAN_HOLD_UPDATABLE_FIELDS_EXPIRATION_DATE) {
            return new ObjectFactory().createShareHoldUpdateableFieldsExpirationDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));

        } else if (dateType == DateType.NAME_UPDATABLE_FIELDS_EXPIRATION_DATE) {
            return new ObjectFactory().createNameUpdateableFieldsExpirationDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));
        } else if (dateType == DateType.EFT_UPDATABLE_FIELDS_EFFECTIVE_DATE) {
            return new ObjectFactory().createEftTransferUpdateableFieldsEffectiveDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));
        } else if (dateType == DateType.EFT_UPDATABLE_FIELDS_EXPIRATION_DATE) {
            return new ObjectFactory().createEftUpdateableFieldsExpirationDate(
                DatatypeFactory.newDefaultInstance()
                    .newXMLGregorianCalendarDate(
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth(),
                        DatatypeConstants.FIELD_UNDEFINED));
        }

        return null;
    }
}
