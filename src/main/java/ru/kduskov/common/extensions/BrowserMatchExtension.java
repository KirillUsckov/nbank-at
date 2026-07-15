package ru.kduskov.common.extensions;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.kduskov.common.annotations.Browser;
import ru.kduskov.common.confs.Config;
import ru.kduskov.common.enums.ConfigParams;

import java.util.Arrays;

public class BrowserMatchExtension implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Browser browsers = context.getElement()
                .map(el -> el.getAnnotation(Browser.class))
                .orElse(null);
        if(browsers == null) {
            return ConditionEvaluationResult.enabled("No browser conditions");
        }
        if (
                Arrays.stream(browsers.value())
                        .anyMatch(
                                browser -> browser.getValue().equals(Config.getProperty(ConfigParams.UI_BROWSER))
                        )
        ) {
            return ConditionEvaluationResult.enabled("Browser in config equals browser in condition");
        }
        return ConditionEvaluationResult.disabled("Browser in config is not equal browser in condition");
    }
}
