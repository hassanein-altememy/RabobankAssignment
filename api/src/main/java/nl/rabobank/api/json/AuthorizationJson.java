package nl.rabobank.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import nl.rabobank.authorizations.Authorization;

import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = AuthorizationJson.AuthorizationJsonBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationJson {

    @NotNull
    private String grantorName;

    @NotNull
    private String granteeName;

    @NotNull
    private String accountNumber;

    @NotNull
    private Authorization access;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AuthorizationJsonBuilder {

    }
}
