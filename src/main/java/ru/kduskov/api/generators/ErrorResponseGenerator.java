package ru.kduskov.api.generators;

import org.eclipse.jetty.http.HttpStatus;
import ru.kduskov.api.models.body.response.general.ErrorResponseBody;
import ru.kduskov.common.confs.Config;
import ru.kduskov.common.enums.ConfigParams;

public class ErrorResponseGenerator {
    private ErrorResponseGenerator(){
    }

    public static ErrorResponseBody generate(HttpStatus.Code code, String path) {
        return ErrorResponseBody.builder()
                .error(code.getMessage())
                .path(Config.getProperty(ConfigParams.API_VERSION) + path)
                .status(code.getCode())
                .build();
    }
}
