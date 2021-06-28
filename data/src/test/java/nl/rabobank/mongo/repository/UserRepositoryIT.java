package nl.rabobank.mongo.repository;

import nl.rabobank.mongo.MongoConfiguration;
import nl.rabobank.mongo.entities.Account;
import nl.rabobank.mongo.entities.AccountType;
import nl.rabobank.mongo.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User repository database test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= MongoConfiguration.class)
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testAddAndFindUsers() {
        final List<Account> hassaneinAccounts = Arrays.asList(
            Account.builder()
                .accountHolderName("Hassanein")
                .accountNumber("NL44RABO0123456789")
                .type(AccountType.PAYMENT)
                .balance(100.00)
                .build(),
            Account.builder()
                .accountHolderName("Hassanein")
                .accountNumber("NL44RABO0123456788")
                .type(AccountType.SAVING)
                .balance(1000.00)
                .build()
        );

        final List<Account> alvinAccounts = Arrays.asList(
                Account.builder()
                        .accountHolderName("Alvin")
                        .accountNumber("NL44RABO0123451234")
                        .type(AccountType.PAYMENT)
                        .balance(200.00)
                        .build(),
                Account.builder()
                        .accountHolderName("Alvin")
                        .accountNumber("NL44RABO0123455555")
                        .type(AccountType.SAVING)
                        .balance(10000.00)
                        .build()
        );

        final List<User> users = Arrays.asList(
            User.builder()
                .name("Hassanein")
                .accounts(hassaneinAccounts)
                .build(),
            User.builder()
                .name("Alvin")
                .accounts(alvinAccounts)
                .build()
        );

        this.userRepository.saveAll(users);
        assertThat(this.userRepository.findAll()).isNotEmpty();
        assertThat(this.userRepository.findAll().size()).isEqualTo(2);
    }

}
