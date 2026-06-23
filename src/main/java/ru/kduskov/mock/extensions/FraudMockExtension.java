package ru.kduskov.mock.extensions;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import ru.kduskov.mock.annotations.FraudMockStatus;
import ru.kduskov.mock.enums.FraudStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FraudMockExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws IOException {

        FraudMockStatus annotation =
                AnnotationSupport.findAnnotation(
                                context.getRequiredTestMethod(),
                                FraudMockStatus.class)
                        .orElseGet(() ->
                                AnnotationSupport.findAnnotation(
                                                context.getRequiredTestClass(),
                                                FraudMockStatus.class)
                                        .orElse(null));

        configureFraudMock(annotation.value());
    }

    private void configureFraudMock(FraudStatus status) throws IOException {
        var body = new String(
                Objects.requireNonNull(
                        getClass().getClassLoader()
                                .getResourceAsStream(status.getResponseFile())
                ).readAllBytes(),
                StandardCharsets.UTF_8
        );
        stubFor(post(urlEqualTo("/fraud-check"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }

}
