package ru.kduskov.db.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.enums.Role;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.db.models.dao.CustomerDao;

public class CustomerDaoAssert extends BaseDbAssert<CustomerDaoAssert, CustomerDao> {
    protected CustomerDaoAssert(CustomerDao actual, SoftAssertions softly) {
        super(actual, CustomerDaoAssert.class, softly);
    }

    public static CustomerDaoAssert assertThat(CustomerDao actual, SoftAssertions softly) {
        return new CustomerDaoAssert(actual,  softly);
    }


    public CustomerDaoAssert matches(ChangeUserProfileResponseBody changeUserResponse) {
        return (CustomerDaoAssert) nameEquals(changeUserResponse.getName())
                .usernameEquals(changeUserResponse.getUsername())
                .roleEquals(changeUserResponse.getRole())
                .idEquals(changeUserResponse.getId());
    }

    public CustomerDaoAssert matches(UserProfileResponseBody userResponse) {
        return (CustomerDaoAssert) nameEquals(userResponse.getName())
                .usernameEquals(userResponse.getUsername())
                .roleEquals(userResponse.getRole())
                .idEquals(userResponse.getId());
    }

    public CustomerDaoAssert equals(CustomerDao expected) {
        return (CustomerDaoAssert) nameEquals(expected.getName())
                .usernameEquals(expected.getUsername())
                .roleEquals(expected.getRole())
                .passwordEquals(expected.getPassword())
                .idEquals(expected.getId())
                .dateCreatedEquals(expected.getCreatedAt())
                .dateUpdatedEquals(expected.getUpdatedAt());
    }

    public CustomerDaoAssert nameEquals(String expectedName) {
        isEqualTo(actual.getName(), expectedName, String.format("Expected name %s but was %s", expectedName, actual.getName()));
        return this;
    }

    public CustomerDaoAssert usernameEquals(String expectedUsername) {
        isEqualTo(actual.getUsername(), expectedUsername, String.format("Expected username %s but was %s", expectedUsername, actual.getUsername()));
        return this;
    }

    public CustomerDaoAssert passwordEquals(String expectedPassword) {
        isEqualTo(actual.getPassword(), expectedPassword, String.format("Expected password %s but was %s", expectedPassword, actual.getPassword()));
        return this;
    }

    public CustomerDaoAssert roleEquals(Role expectedRole) {
        isEqualTo(actual.getRole(), expectedRole, String.format("Expected role %s but was %s", expectedRole, actual.getRole()));
        return this;
    }
}
