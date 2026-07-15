package ru.kduskov.common.extensions;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TimerExtension implements BeforeEachCallback, AfterEachCallback {
    private final Map<String, Long> startTimes = new HashMap<>();
    private final Map<String, Long> endTimes = new HashMap<>();

    // Добавьте счетчик экземпляров
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger(0);
    private final int instanceId;

    public TimerExtension() {
        this.instanceId = INSTANCE_COUNTER.incrementAndGet();
        System.out.printf("TimerExtension instance #%d created%n", instanceId);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        var testName = getFullTestName(context);
        System.out.printf("Instance #%d - beforeTestExecution for: %s%n", instanceId, testName);
        startTimes.put(testName, System.currentTimeMillis());
        System.out.printf("Thread '%s' with test '%s' started%n", Thread.currentThread().getName(), testName);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var testName = getFullTestName(context);
        endTimes.put(testName, System.currentTimeMillis());
        var duration = endTimes.get(testName) - startTimes.get(testName);
        System.out.printf("Thread '%s' with test '%s' ended with duration %s%n", Thread.currentThread().getName(), testName, duration);

    }

    private String getFullTestName(ExtensionContext context) {
        var baseName = String.format(
                "%s.%s",
                context.getRequiredTestClass().getSimpleName(),
                context.getRequiredTestMethod().getName()
        );

        // Используем displayName, который содержит параметры для @ValueSource
        var displayName = context.getDisplayName();

        // Если displayName не стандартный (не равен имени метода), значит есть параметры
        if (!displayName.equals(context.getRequiredTestMethod().getName())) {
            baseName += "[" + displayName + "]";
        }

        return baseName;
    }
}
