package nl.rabobank.account;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class SavingsAccount implements Account
{
    String accountNumber;
    String accountHolderName;
    Double balance;
}
