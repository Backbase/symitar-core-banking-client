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
    admin-password: ADMIN_PASSWORD
    device-type: DEVICE_TYPE
    device-number: 12345
```

```java

@Data
@Configuration
@ConfigurationProperties("symitar.client")
public class SymitarCoreBankingClientProperties {

    private String baseUrl;
    private String adminPassword;
    private String deviceType;
    private short deviceNumber;

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