package ru.kduskov.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.Getter;

public class MockRunner {
    @Getter
    private static WireMockServer wireMockServer;

    public static void setUpWireMock() {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(
                    WireMockConfiguration.wireMockConfig().port(8080)
            );

            wireMockServer.start();

            WireMock.configureFor("0.0.0.0", 8080);
        }
    }

    public static void closeWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }
}