package ru.kduskov.common.listener;

import groovy.util.logging.Slf4j;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import ru.kduskov.api.generators.LoginRequestGenerator;
import ru.kduskov.api.steps.AccountSteps;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.api.steps.LoginSteps;
import ru.kduskov.api.steps.UserSteps;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@lombok.extern.slf4j.Slf4j
@Slf4j
public class GlobalCleanupListener implements TestExecutionListener {
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        long setupStart = System.currentTimeMillis();
        CompletableFuture<Void> cleanup = CompletableFuture.runAsync(() -> {
            for (var user : AdminSteps.getAllUsers()) {
                try {
                    var userToken = LoginSteps.login(LoginRequestGenerator.generate(user));
                    var customer = new UserSteps(userToken).getCustomer();
                    var id = customer.getId();
                    // Параллельное удаление аккаунтов
                    customer.getAccounts().parallelStream()
                            .forEach(account ->
                                    AccountSteps.deleteAccount(userToken, account.getId()));

                    AdminSteps.deleteUser(id);
                } catch (Exception e) {
                    System.err.println("Error cleaning user: " + e.getMessage());
                }
            }
        });

        // Ожидаем завершения, но не более 3 секунд
        try {
            cleanup.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            GlobalCleanupListener.log.warn("Cleanup timeout exceeded, forcing continuation");
        }

        long setupEnd = System.currentTimeMillis();
        GlobalCleanupListener.log.info("GLOBAL_CLEANUP end [%s] %s - duration: %dms%n",
                Thread.currentThread().getName(), "setUpTestData", (setupEnd - setupStart));

    }
}