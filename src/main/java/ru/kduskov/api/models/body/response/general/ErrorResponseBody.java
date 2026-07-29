package ru.kduskov.api.models.body.response.general;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseBody {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
