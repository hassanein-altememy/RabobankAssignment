package nl.rabobank.api;


import nl.rabobank.api.json.AuthorizationJson;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.entities.Account;
import nl.rabobank.mongo.entities.AccountType;
import nl.rabobank.mongo.entities.User;
import nl.rabobank.mongo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

/**
 * Rest controller responsible for authorizing accounts.
 */
@RestController
@RequestMapping("/rabobank-assignment/authorization")
public class AuthorizationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationResource.class);

    private final UserRepository userRepository;

    @Autowired
    public AuthorizationResource(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authorization grantee to access grantor payment accounts.
     *
     * @param authorization contains the authorization information.
     */
    @PostMapping("/payment-account/add/v1")
    public void AddAuthorizationToPaymentAccount(@Valid @RequestBody final AuthorizationJson authorization) {

        logInfo("Grantor {} grants the grantee {}, {} access to his payment account {}.", authorization);

        addAuthorization(authorization, AccountType.PAYMENT);
    }

    /**
     * Authorization grantee to access grantor saving accounts.
     *
     * @param authorization contains the authorization information.
     */
    @PostMapping("/saving-account/add/v1")
    public void AddAuthorizationToSavingAccount(@Valid @RequestBody final AuthorizationJson authorization) {

        logInfo("Grantor {} grants the grantee {}, {} access to his saving account {}.", authorization);

        addAuthorization(authorization, AccountType.SAVING);
    }

    private void addAuthorization(final AuthorizationJson authorization, final AccountType accountType) {
        final User grantee = getUser(authorization.getGranteeName());
        if (Authorization.READ.equals(authorization.getAccess())) {
            grantee.getPermittedReadAccounts().add(mapAccount(authorization, accountType));
        }
        if (Authorization.WRITE.equals(authorization.getAccess())) {
            grantee.getPermittedWriteAccounts().add(mapAccount(authorization, accountType));
        }
        this.userRepository.save(grantee);
    }

    /**
     * Get the grantor account to be authorized, otherwise the given account is not correct.
     *
     * @param authorization authorization object containing the account to be authorized.
     * @return the Grantor account to be authorized
     * @throws HttpClientErrorException if the account to be authorized not a grantor account.
     */
    private Account getGrantorAccount(final AuthorizationJson authorization) {
        final User grantor = getUser(authorization.getGrantorName());
        return grantor.getAccounts().stream()
                .filter(account -> authorization.getAccountNumber().equals(account.getAccountNumber()))
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The account is not a grantor account!"));
    }

    private Account mapAccount(final AuthorizationJson authorization, final AccountType accountType) {

        final Account grantorAccount = getGrantorAccount(authorization);
        return  Account.builder()
            .accountNumber(grantorAccount.getAccountNumber())
            .accountHolderName(grantorAccount.getAccountHolderName())
            .balance(grantorAccount.getBalance())
            .type(accountType)
            .build();
    }

    private void logInfo(final String message, final AuthorizationJson authorization) {
        LOG.info(message,
                authorization.getGrantorName(),
                authorization.getGranteeName(),
                authorization.getAccess(),
                authorization.getAccountNumber());
    }

    private User getUser(final String name) {
        return this.userRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User does not exist!"));
    }

}