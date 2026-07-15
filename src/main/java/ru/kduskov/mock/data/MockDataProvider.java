package ru.kduskov.mock.data;

import lombok.experimental.UtilityClass;
import ru.kduskov.mock.models.FraudMockResponse;
import ru.kduskov.mock.enums.FraudStatus;
import ru.kduskov.ui.utils.JsonParser;

@UtilityClass
public class MockDataProvider {
    public FraudMockResponse fraudResponse(FraudStatus status) {
        return JsonParser.parse(
                status.getResponseFile(),
                FraudMockResponse.class
        );
    }
}
