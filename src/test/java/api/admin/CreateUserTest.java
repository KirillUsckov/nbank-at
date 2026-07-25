package api.admin;

import common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.kduskov.api.constants.GenerationsRegexes;
import ru.kduskov.common.enums.GenerationsRules;
import ru.kduskov.common.enums.MatchingCondition;
import ru.kduskov.common.generators.RandomData;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.db.steps.DbAssertionSteps;
import ru.kduskov.db.steps.SqlSteps;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static common.Constans.FIRST_USER_ID;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class CreateUserTest extends BaseTest {
    private DbAssertionSteps dbAssertionSteps;

    @BeforeEach
    public void initAssertionClasses() {
        this.dbAssertionSteps = new DbAssertionSteps(softly);
    }

    @Test
    @DisplayName("Admin creates a user when the request is valid")
    public void shouldCreateUserWhenRequestIsValid() {
        var user = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        var authToken = AdminSteps.createUser(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), user);

        this.stringAssertionsSteps.assertTextMatches(authToken, Pattern.compile("^Basic (.+)"));
        var customerDao = SqlSteps.findCustomerByUsername(user.getUsername());
        dbAssertionSteps.assertCustomerDaoMatchCreateUserRequest(customerDao.orElseThrow(), user);
    }

    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("provideInvalidRequests")
    @DisplayName("User creation is rejected when the request has invalid param")
    public void shouldRejectUserCreationWhenRequestIsInvalid(CreateUserRequestBody request, String caseName) {
        var authToken = AdminSteps.createUser(
                RequestSpecs.adminSpec(),
                ResponseSpecs.badRequest(),
                request
        );

        this.stringAssertionsSteps.assertTextEqualsTo(null, authToken);
    }

    @Test
    @DisplayName("User creation is rejected when the request is empty")
    public void shouldRejectUserCreationWhenRequestIsEmpty() {
        var authToken = AdminSteps.createUser(RequestSpecs.adminSpec(), ResponseSpecs.badRequest(), null);

        this.stringAssertionsSteps.assertTextEqualsTo(null, authToken);
    }

    @Test
    @UserSession
    @DisplayName("User creation is rejected when requested by a regular user")
    public void shouldRejectUserCreationWhenRequestedByRegularUser() {
        var firstUser = SessionStorage.getUser(FIRST_USER_ID);
        var authToken = AdminSteps.createUser(
                RequestSpecs.userSpec(firstUser.getToken()),
                ResponseSpecs.accessForbidden(),
                null
        );

        this.stringAssertionsSteps.assertTextEqualsTo(null, authToken);
    }

    @Test
    @DisplayName("User creation is rejected when request without auth")
    public void shouldRejectUserCreationWhenRequestWithoutAuth() {
        var authToken = AdminSteps.createUser(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.unauthorized(),
                RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class)
        );

        this.stringAssertionsSteps.assertTextEqualsTo(null, authToken);
    }

    private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
                Arguments.of(
                        requestWithUsername(EMPTY),
                        "empty username"
                ),
                Arguments.of(
                        requestWithUsername(RandomData.getNotMatchingRegexString(
                                MatchingCondition.RANDOM, GenerationsRegexes.USERNAME
                        )),
                        "username has invalid format"
                ),
                Arguments.of(
                        requestWithUsername(
                                RandomData.getNotMatchingRegexString(
                                        MatchingCondition.LENGTH_LESS, GenerationsRegexes.USERNAME
                                )
                        ),
                        "username below minimum length"
                ),
                Arguments.of(
                        requestWithUsername(
                                RandomData.getNotMatchingRegexString(
                                        MatchingCondition.LENGTH_MORE, GenerationsRegexes.USERNAME
                                )
                        ),
                        "username above the maximum length"
                ),
                Arguments.of(
                        requestWithPassword(EMPTY),
                        "empty password"
                )
                ,
                Arguments.of(
                        requestWithPassword(
                                RandomData.getNotMatchingCondition(
                                        MatchingCondition.RANDOM, GenerationsRules.PASSWORD
                                )
                        ),
                        "password has invalid format"
                ),

                Arguments.of(
                        requestWithPassword(
                                RandomData.getNotMatchingCondition(
                                        MatchingCondition.LENGTH_LESS, GenerationsRules.PASSWORD
                                )
                        ),
                        "password below minimum length"
                )
        );
    }

    private static CreateUserRequestBody requestWithUsername(String username) {
        var request = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        request.setUsername(username);
        return request;
    }

    private static CreateUserRequestBody requestWithPassword(String password) {
        var request = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        request.setPassword(password);
        return request;
    }
}
