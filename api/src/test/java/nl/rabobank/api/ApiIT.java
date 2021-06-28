package nl.rabobank.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.api.json.AuthorizationJson;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.entities.Account;
import nl.rabobank.mongo.entities.AccountType;
import nl.rabobank.mongo.entities.User;
import nl.rabobank.mongo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Api Integration test
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Resource
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() {
        //SetUp database with data
        initDatabase();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void testAddReadAccessToPaymentAccount() throws Exception {
        //Hassanein create read authorization for his payment account to Alvin
        final AuthorizationJson authorization = AuthorizationJson.builder()
                .accountNumber("NL44RABO0123456789")
                .access(Authorization.READ)
                .grantorName("Hassanein")
                .granteeName("Alvin")
                .build();

        mockMvc.perform(post("/rabobank-assignment/authorization/payment-account/add/v1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authorization)))
            .andExpect(status().is2xxSuccessful());


        final String expectedPermittedAccounts = objectMapper.writeValueAsString(Arrays.asList(
                PaymentAccount.builder()
                    .accountHolderName("Hassanein")
                    .accountNumber("NL44RABO0123456789")
                    .balance(100.00)
                    .build()));
        //Alvin should have this account in his read permitted payment accounts
        mockMvc.perform(get("/rabobank-assignment/user/read-permitted/payment-accounts/v1?userName={userName}", "Alvin"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedPermittedAccounts));

    }

    @Test
    public void testAddWriteAccessToPaymentAccount() throws Exception {
        //Alvin create read authorization for his payment account to Hassanein
        final AuthorizationJson authorization = AuthorizationJson.builder()
                .accountNumber("NL44RABO0123451234")
                .access(Authorization.WRITE)
                .grantorName("Alvin")
                .granteeName("Hassanein")
                .build();

        mockMvc.perform(post("/rabobank-assignment/authorization/payment-account/add/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorization)))
                .andExpect(status().is2xxSuccessful());


        final String expectedPermittedAccounts = objectMapper.writeValueAsString(Arrays.asList(
                PaymentAccount.builder()
                        .accountHolderName("Alvin")
                        .accountNumber("NL44RABO0123451234")
                        .balance(200.00)
                        .build()));
        //Hassanein should have this account in his read permitted payment accounts
        mockMvc.perform(get("/rabobank-assignment/user/write-permitted/payment-accounts/v1?userName={userName}", "Hassanein"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedPermittedAccounts));

    }

    @Test
    public void testAddReadAccessToSavingAccount() throws Exception {
        //Alvin create write authorization for his saving account to Hassanein
        final AuthorizationJson authorization = AuthorizationJson.builder()
                .accountNumber("NL44RABO0123455555")
                .access(Authorization.READ)
                .grantorName("Alvin")
                .granteeName("Hassanein")
                .build();

        mockMvc.perform(post("/rabobank-assignment/authorization/saving-account/add/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorization)))
                .andExpect(status().is2xxSuccessful());

        final String expectedPermittedAccounts = objectMapper.writeValueAsString(Arrays.asList(
                SavingsAccount.builder()
                        .accountHolderName("Alvin")
                        .accountNumber("NL44RABO0123455555")
                        .balance(10000.00)
                        .build()));

        //Hassanein should have this account in his write permitted saving accounts
        mockMvc.perform(get("/rabobank-assignment/user/read-permitted/saving-accounts/v1?userName={userName}", "Hassanein"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedPermittedAccounts));

    }

    @Test
    public void testAddWriteAccessToSavingAccount() throws Exception {
        //Hassanein create write authorization for his saving account to Alvin
        final AuthorizationJson authorization = AuthorizationJson.builder()
                .accountNumber("NL44RABO0123456788")
                .access(Authorization.WRITE)
                .grantorName("Hassanein")
                .granteeName("Alvin")
                .build();

        mockMvc.perform(post("/rabobank-assignment/authorization/saving-account/add/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorization)))
                .andExpect(status().is2xxSuccessful());

        final String expectedPermittedAccounts = objectMapper.writeValueAsString(Arrays.asList(
                SavingsAccount.builder()
                        .accountHolderName("Hassanein")
                        .accountNumber("NL44RABO0123456788")
                        .balance(1000.00)
                        .build()));

        //Alvin should have this account in his write permitted saving accounts
        mockMvc.perform(get("/rabobank-assignment/user/write-permitted/saving-accounts/v1?userName={userName}", "Alvin"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(expectedPermittedAccounts));

    }

    @Test
    public void testAddReadAccessToNotExistingPaymentAccount() throws Exception {
        //Hassanein create read authorization for his payment account to Alvin
        final AuthorizationJson authorization = AuthorizationJson.builder()
                .accountNumber("blabla")
                .access(Authorization.READ)
                .grantorName("Hassanein")
                .granteeName("Alvin")
                .build();

        mockMvc.perform(post("/rabobank-assignment/authorization/payment-account/add/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorization)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddReadAccessToNotExistingUser() throws Exception {
        //Hassanein create read authorization for his payment account to Alvin
        final AuthorizationJson authorization = AuthorizationJson.builder()
                .accountNumber("NL44RABO0123456789")
                .access(Authorization.READ)
                .grantorName("Erwin")
                .granteeName("Alvin")
                .build();

        mockMvc.perform(post("/rabobank-assignment/authorization/payment-account/add/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorization)))
                .andExpect(status().isNotFound());
    }



    private void initDatabase() {
        this.userRepository.deleteAll();
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

    }
}
