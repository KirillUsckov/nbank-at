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
                    WireMockConfiguration.wireMockConfig()
                            .bindAddress("0.0.0.0")
                            .port(8080)
            );

            wireMockServer.start();}
    }

    public static void closeWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }
}