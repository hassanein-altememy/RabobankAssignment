package nl.rabobank.api;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.mongo.entities.AccountType;
import nl.rabobank.mongo.entities.User;
import nl.rabobank.mongo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Rest controller responsible for retrieving permitted accounts.
 */
@RestController
@RequestMapping("/rabobank-assignment/user")
public class UserAccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccountResource.class);

    private final UserRepository userRepository;

    @Autowired
    public UserAccountResource(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/read-permitted/saving-accounts/v1")
    public List<Account> getReadPermittedSavingAccounts(@RequestParam("userName") final String userName) {
        final User user = getUser(userName);
        final List<Account> readPermittedSavingAccounts = mapPermittedSavingAccounts(user.getPermittedReadAccounts());
        LOG.info("{} read permitted saving accounts retrieved of user {}.", readPermittedSavingAccounts.size(),
                userName);
        return readPermittedSavingAccounts;
    }

    @GetMapping("/write-permitted/saving-accounts/v1")
    public List<Account> getWritePermittedSavingAccounts(@RequestParam("userName") final String userName) {
        final User user = getUser(userName);
        final List<Account> writePermittedSavingAccounts = mapPermittedSavingAccounts(user.getPermittedWriteAccounts());
        LOG.info("{} write permitted saving accounts retrieved of user {}.", writePermittedSavingAccounts.size(),
                userName);
        return writePermittedSavingAccounts;
    }

    @GetMapping("/read-permitted/payment-accounts/v1")
    public List<Account> getReadPermittedPaymentAccounts(@RequestParam("userName") final String userName) {
        final User user = getUser(userName);
        final List<Account> readPermittedPaymentAccounts = mapPermittedPaymentAccounts(user.getPermittedReadAccounts());
        LOG.info("{} read permitted saving accounts retrieved of user {}.", readPermittedPaymentAccounts.size(),
                userName);
        return readPermittedPaymentAccounts;
    }

    @GetMapping("/write-permitted/payment-accounts/v1")
    public List<Account> getWritePermittedPaymentAccounts(@RequestParam("userName") final String userName) {
        final User user = getUser(userName);
        final List<Account> writePermittedPaymentAccounts =
                mapPermittedPaymentAccounts(user.getPermittedWriteAccounts());
        LOG.info("{} read permitted saving accounts retrieved of user {}.", writePermittedPaymentAccounts.size(),
                userName);
        return writePermittedPaymentAccounts;
    }

    private List<Account> mapPermittedSavingAccounts(final List<nl.rabobank.mongo.entities.Account> dbAccounts) {
        return dbAccounts.stream()
                .filter(account -> AccountType.SAVING == account.getType())
                .map(account -> mapSavingAccount(account))
                .collect(Collectors.toList());
    }

    private Account mapSavingAccount(final nl.rabobank.mongo.entities.Account account) {
        return SavingsAccount.builder()
                .accountHolderName(account.getAccountHolderName())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

    private List<Account> mapPermittedPaymentAccounts(final List<nl.rabobank.mongo.entities.Account> dbAccounts) {
        return dbAccounts.stream()
                .filter(account -> AccountType.PAYMENT == account.getType())
                .map(account -> mapPaymentAccount(account))
                .collect(Collectors.toList());
    }

    private Account mapPaymentAccount(final nl.rabobank.mongo.entities.Account account) {
        return PaymentAccount.builder()
                .accountHolderName(account.getAccountHolderName())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

    private User getUser(final String name) {
        return this.userRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User does not exist!"));
    }

}
