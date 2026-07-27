package ru.kduskov.ui.extensions;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

/**
 * Extension works only with classes with 'uitest' in name
 */
public class AllureScreenshotExtension implements TestExecutionExceptionHandler {
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        String className = context.getRequiredTestClass().getSimpleName();
        if (className.toLowerCase().contains("uitest")) {
            try {
                // Take the binary screenshot from Selenide and forward it straight to Allure
                byte[] screenshot = Selenide.screenshot(OutputType.BYTES);
                if (screenshot != null) {
                    Allure.attachment("UI Failure Screenshot", new ByteArrayInputStream(screenshot));
                }
            } catch (Exception e) {
                // Prevent screenshot failures from masking the original test exception
            }
        }
        throw throwable; // Always rethrow the original test exception
    }
}