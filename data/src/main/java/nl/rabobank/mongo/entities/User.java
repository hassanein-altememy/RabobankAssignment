package nl.rabobank.mongo.entities;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * User embedded data.
 */
@Value
@Builder(toBuilder = true)
@Document
public class User {
    @Id
    private String id;

    /**
     * User name.
     */
    private String name;

    /**
     * User own accounts.
     */
    @Singular
    private List<Account> accounts;

    /**
     * Read permitted accounts.
     */
    @Singular
    private List<Account> permittedReadAccounts;

    /**
     * Write permitted accounts.
     */
    @Singular
    private List<Account> permittedWriteAccounts;
}