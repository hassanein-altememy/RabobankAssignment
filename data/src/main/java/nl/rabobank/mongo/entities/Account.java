package nl.rabobank.mongo.entities;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Account {
    private String accountNumber;
    private String accountHolderName;
    private Double balance;
    private AccountType type;
}