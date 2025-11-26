package ru.kduskov.common.storage;

import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.ui.models.UserModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SessionStorage {
    private static final SessionStorage INSTANCE = new SessionStorage();
    private final LinkedHashMap<String, UserModel> userMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, List<AccountResponseBody>> accountMap = new LinkedHashMap<>();

    private SessionStorage() {
    }

    public static SessionStorage getInstance() {
        return INSTANCE;
    }

    public static void insertUserAccounts(String username, List<AccountResponseBody> accounts) {
        INSTANCE.accountMap.put(username, accounts);
    }

    public static List<AccountResponseBody> getUserAccounts(int idx) {
        return INSTANCE.accountMap.get(INSTANCE.accountMap.keySet().stream().toList().get(idx - 1));
    }

    public static List<AccountResponseBody> getUserAccounts(String username) {
        return INSTANCE.accountMap.get(username);
    }

    public static void insertUser(UserModel userModel) {
        INSTANCE.userMap.put(userModel.getUsername(), userModel);
        INSTANCE.userStepsMap.put(userModel.getUsername(), new UserSteps(userModel.getToken()));
    }

    public static void insertUsers(List<UserModel> userModels) {
        userModels.forEach(SessionStorage::insertUser);
    }

    public static List<UserModel> getAllUsers() {
        return INSTANCE.userMap.values().stream().toList();
    }

    public static UserModel getUser(int idx) {
        var key = INSTANCE.userMap.keySet().toArray()[idx-1];
        return INSTANCE.userMap.get(key);
    }

    public static UserSteps getUserSteps(int idx) {
        return INSTANCE.userStepsMap.get(INSTANCE.userStepsMap.keySet().stream().toList().get(idx - 1));
    }


    public static UserSteps getUserSteps(String username) {
        return INSTANCE.userStepsMap.get(username);
    }

    public static AccountResponseBody getUserAccount(String username, int accId) {
        System.out.println("getUserAccount");
        System.out.println("username: " + username);
        System.out.println("accId: " + accId);
        System.out.println("accounts keys: " + String.join(", ", INSTANCE.accountMap.keySet()));
        System.out.println("accounts values: " + INSTANCE.accountMap.values().stream().map(el-> el.stream().map(AccountResponseBody::getAccountNumber).collect(Collectors.joining(", "))).collect(Collectors.joining("; ")));
        var accs = INSTANCE.accountMap.get(username);
        return accs.get(accId - 1);

    }

    public static void clear() {
        INSTANCE.userMap.clear();
        INSTANCE.accountMap.clear();
        INSTANCE.userStepsMap.clear();
    }
}
