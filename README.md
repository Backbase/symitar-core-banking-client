# symitar-core-banking-client

This project contains a SOAP client library that can be used to call Symitar web services. WSDLs and XSDs are located
in: `src/main/resources/wsdl`

This project can be utilized in your Backbase integration services as a Maven dependency. Simply include the following
maven coordinates in the `dependency` section of your service's `pom.xml`

```aidl
    <groupId>com.backbase.accelerators</groupId>
    <artifactId>symitar-core-banking-client</artifactId>
    <version>1.0.0</version>
```

## Build this project

From the root directory of this project, run:

```mvn clean install```

This will compile the project and generate Java classes from the WSDLs and XSDs in the resources folder. The generated
classes can be found in: `target/generated-sources`

### Example usage - Defining `application.yml` configuration:

Properties should be defined in your Backbase integration service as follows. Please obtain actual configuration values
from your customer.

```yaml
symitar:
  client:
    baseUrl: http://symitar.webservice.host:8087/SymXchange/2020.00
    admin-credientials: ADMIN_CREDENTIALS
    device-type: DEVICE_TYPE
    device-number: 12345
```

```java

@Data
@Configuration
@ConfigurationProperties("symitar.client")
public class SymitarCoreBankingClientProperties {

    private String baseUrl;
    private String adminCredentials;
    private String deviceType;
    private short deviceNumber;

    public SymitarRequestSettings toSymitarRequestSettings() {
        /* This example uses AdministrativeCredentials, but there are other credential types such as 
        UserNumberCredentials, TokenCredentials, AccountNumberCredentials, etc. 
        Review the AdminCredentialsChoice and CredentialsChoice classes to understand what options are available to set */

        AdministrativeCredentials administrativeCredentials = new AdministrativeCredentials();
        administrativeCredentials.setPassword(adminCredentials);

        AdminCredentialsChoice adminCredentialsChoice = new AdminCredentialsChoice();
        adminCredentialsChoice.setAdministrativeCredentials(administrativeCredentials);

        CredentialsChoice credentialsChoice = new CredentialsChoice();
        credentialsChoice.setAdministrativeCredentials(administrativeCredentials);

        DeviceInformation deviceInformation = new DeviceInformation();
        deviceInformation.setDeviceNumber(deviceNumber);
        deviceInformation.setDeviceType(deviceType);

        SymitarRequestSettings symitarRequestSettings = new SymitarRequestSettings();
        symitarRequestSettings.setBaseUrl(baseUrl);
        symitarRequestSettings.setAdminCredentialsChoice(adminCredentialsChoice);
        symitarRequestSettings.setCredentialsChoice(credentialsChoice);
        symitarRequestSettings.setDeviceInformation(deviceInformation);
        symitarRequestSettings.setMessageId(UUID.randomUUID().toString());

        return symitarRequestSettings;
    }
}
```

### Example usage - Defining a Spring Bean in Your Integration Service:

The below example wires up the `AccountClient`, `TransactionClient` and `TransferClient` as Spring beans.

```java

@Configuration
public class SymitarCoreBankingClientConfiguration {

    @Bean
    @SneakyThrows
    public AccountClient accountClient(SymitarCoreBankingClientProperties symitarCoreBankingClientProperties) {
        return new AccountClient(
                getAccountService(symitarCoreBankingClientProperties),
                symitarCoreBankingClientProperties.toSymitarRequestSettings());
    }

    @Bean
    @SneakyThrows
    public TransactionClient transactionClient(SymitarCoreBankingClientProperties symitarCoreBankingClientProperties) {
        return new TransactionClient(
                getAccountService(symitarCoreBankingClientProperties),
                symitarCoreBankingClientProperties.toSymitarRequestSettings());
    }

    @Bean
    @SneakyThrows
    public TransferClient transferClient(SymitarCoreBankingClientProperties symitarCoreBankingClientProperties) {
        return new TransferClient(
                getAccountService(symitarCoreBankingClientProperties),
                getTransactionsService(symitarCoreBankingClientProperties),
                symitarCoreBankingClientProperties.toSymitarRequestSettings());
    }

    @SneakyThrows
    private AccountService getAccountService(SymitarCoreBankingClientProperties symitarCoreBankingClientProperties) {
        String url = buildFinalUrl(symitarCoreBankingClientProperties.getBaseUrl(), Endpoints.ACCOUNT_ENDPOINT);
        return new AccountService_Service(new URL(url)).getAccountServicePort();
    }

    @SneakyThrows
    private TransactionsService getTransactionsService(SymitarCoreBankingClientProperties symitarCoreBankingClientProperties) {
        String url = buildFinalUrl(symitarCoreBankingClientProperties.getBaseUrl(), Endpoints.TRANSACTIONS_ENDPOINT);
        return new TransactionsService_Service(new URL(url)).getTransactionsServicePort();
    }

    private String buildFinalUrl(String baseUrl, String endpoint) {
        return baseUrl.concat(endpoint);
    }
}
```

### Additional configuration options:

The `SymitarRequestSettings` supports additional nested configuration settings for the following:

- Stop check payments
- Wire transfers

This is done to promote feature-specific configurability that may vary across customers. For example, a stop check
payment fee withdrawal code for one institution may be `1`, but for another institution it may be `3`.

Leveraging the example above, you can define additional properties in your `application.yml` as follows (this example
will use stop check payments):

```yaml
symitar:
  client:
    baseUrl: http://symitar.webservice.host:8087/SymXchange/2020.00
    admin-password: ADMIN_PASSWORD
    device-type: DEVICE_TYPE
    device-number: 12345
    stopCheckPaymentOptions: # Adding stop check payment properties
      withdrawalFeeAmount: 3.00
      withdrawalFeeReasonText: Withdrawal fee for stop check payment
      withdrawalFeeCode: 1
      generalLedgerClearingCode: 33

```

And update `SymitarCoreBankingClientProperties`:

```java

@Data
@Configuration
@ConfigurationProperties("symitar.client")
public class SymitarCoreBankingClientProperties {

    private String baseUrl;
    private String adminPassword;
    private String deviceType;
    private short deviceNumber;
    private StopCheckPaymentOptions stopCheckPaymentOptions; // New class member to bind application.yml properties to

    // Static nested class to hold stop check payment configuration
    @Data
    public static class StopCheckPaymentOptions {

        private BigDecimal withdrawalFeeAmount;
        private String withdrawalFeeReasonText;
        private short withdrawalFeeCode;
        private short generalLedgerClearingCode;
    }

    public SymitarRequestSettings toSymitarRequestSettings() {
        AdministrativeCredentials administrativeCredentials = new AdministrativeCredentials();
        administrativeCredentials.setPassword(adminPassword);

        AdminCredentialsChoice adminCredentialsChoice = new AdminCredentialsChoice();
        adminCredentialsChoice.setAdministrativeCredentials(administrativeCredentials);

        CredentialsChoice credentialsChoice = new CredentialsChoice();
        credentialsChoice.setAdministrativeCredentials(administrativeCredentials);

        DeviceInformation deviceInformation = new DeviceInformation();
        deviceInformation.setDeviceNumber(deviceNumber);
        deviceInformation.setDeviceType(deviceType);

        // Mapping stop check payment properties from application.yml to SymitarRequestSettings.StopCheckPaymentSettings object
        SymitarRequestSettings.StopCheckPaymentSettings stopCheckPaymentSettings = new SymitarRequestSettings.StopCheckPaymentSettings();
        stopCheckPaymentSettings.setWithdrawalFeeAmount(stopCheckPaymentOptions.getWithdrawalFeeAmount());
        stopCheckPaymentSettings.setWithdrawalFeeReasonText(stopCheckPaymentOptions.getWithdrawalFeeReasonText());
        stopCheckPaymentSettings.setWithdrawalFeeCode(stopCheckPaymentOptions.getWithdrawalFeeCode());
        stopCheckPaymentSettings.setGeneralLedgerClearingCode(stopCheckPaymentOptions.getGeneralLedgerClearingCode());

        SymitarRequestSettings symitarRequestSettings = new SymitarRequestSettings();
        symitarRequestSettings.setBaseUrl(baseUrl);
        symitarRequestSettings.setAdminCredentialsChoice(adminCredentialsChoice);
        symitarRequestSettings.setCredentialsChoice(credentialsChoice);
        symitarRequestSettings.setDeviceInformation(deviceInformation);
        symitarRequestSettings.setMessageId(UUID.randomUUID().toString());
        symitarRequestSettings.setStopCheckPaymentSettings(stopCheckPaymentSettings);

        return symitarRequestSettings;
    }
}
```

See `SymitarRequestSettings.java` class for all available configuration settings.
