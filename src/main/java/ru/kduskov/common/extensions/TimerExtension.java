package ru.kduskov.common.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashMap;
import java.util.Map;

public class TimerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private Map<String, Long> startTimes = new HashMap<>();
    private Map<String, Long> endTimes = new HashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        var testName = context.getRequiredTestClass().getPackageName() + "." + context.getRequiredTestMethod().getName();
        startTimes.put(testName, System.currentTimeMillis());
        System.out.printf("Thread '%s' with test '%s' started%n", Thread.currentThread().getName(), testName);
    }
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        var testName = context.getRequiredTestClass().getPackageName() + "." + context.getRequiredTestMethod().getName();
        endTimes.put(testName, System.currentTimeMillis());
        var duration = endTimes.get(testName) - startTimes.get(testName);
        System.out.printf("Thread '%s' with test '%s' ended with duration %s%n", Thread.currentThread().getName(), testName, duration);

    }
}
