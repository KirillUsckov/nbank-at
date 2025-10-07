import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.kduskov.enums.Role;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.CreateUserRequestBody;
import ru.kduskov.requests.CreateUserRequest;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

public class BaseTest {
    protected static String userAuthToken;
    protected SoftAssertions softly;

    @BeforeAll
    public static void setUpTestUser() {
        userAuthToken = createRandomUser();
    }

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        this.softly.assertAll();
    }


    protected static String createRandomUser() {
        return new CreateUserRequest(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
                .post(
                        CreateUserRequestBody.builder()
                                .username(RandomData.getUsername())
                                .password(RandomData.getPassword())
                                .role(Role.USER).build()
                ).extract().header("Authorization");
    }
}
