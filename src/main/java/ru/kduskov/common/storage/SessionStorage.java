package ru.kduskov.common.storage;

import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.ui.models.UserModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SessionStorage {
    /*
        ThreadLocal даёт каждому потоку свою отдельную копию значения - при первом вызове INSTANCE.get()
        в этом потоке будет вызван supplier from withInitial и создан отдельный SessionStorage для этого потока:
            ThreadLocal.withInitial принимает Supplier<T> и запоминает его.
            При первом вызове INSTANCE.get() в конкретном потоке JUnit/Java вызывает этот supplier
            и сохраняет возвращённое значение в ThreadLocalMap этого потока.
        Реализация: у объекта Thread есть поле ThreadLocalMap; в этой map ключи -
        слабые ссылки на объекты ThreadLocal, значения - сильные ссылки на соответствующие объекты SessionStorage
    */
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);
    private final LinkedHashMap<String, UserModel> userMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, List<AccountResponseBody>> accountMap = new LinkedHashMap<>();

    private SessionStorage() {
    }

    public static SessionStorage getInstance() {
        return INSTANCE.get();
    }

    public static void insertUserAccounts(String username, List<AccountResponseBody> accounts) {
        getInstance().accountMap.put(username, accounts);
    }

    public static List<AccountResponseBody> getUserAccounts(int idx) {
        return getInstance().accountMap.get(getInstance().accountMap.keySet().stream().toList().get(idx - 1));
    }

    public static List<AccountResponseBody> getUserAccounts(String username) {
        return getInstance().accountMap.get(username);
    }

    public static void insertUser(UserModel userModel) {
        getInstance().userMap.put(userModel.getUsername(), userModel);
        getInstance().userStepsMap.put(userModel.getUsername(), new UserSteps(userModel.getToken()));
    }

    public static void insertUsers(List<UserModel> userModels) {
        userModels.forEach(SessionStorage::insertUser);
    }

    public static List<UserModel> getAllUsers() {
        return getInstance().userMap.values().stream().toList();
    }

    public static UserModel getUser(int idx) {
        var key = getInstance().userMap.keySet().toArray()[idx-1];
        return getInstance().userMap.get(key);
    }

    public static UserSteps getUserSteps(int idx) {
        return getInstance().userStepsMap.get(getInstance().userStepsMap.keySet().stream().toList().get(idx - 1));
    }


    public static UserSteps getUserSteps(String username) {
        return getInstance().userStepsMap.get(username);
    }

    public static AccountResponseBody getUserAccount(String username, int accId) {
        System.out.println("AccountMap:" + String.join(", ", getInstance().accountMap.keySet()));
        var map =    getInstance().accountMap;
        var accs = getInstance().accountMap.get(username);
        System.out.println("Accounts : " + accs.stream().map(AccountResponseBody::getAccountNumber).collect(Collectors.joining(", ")));
        return accs.get(accId - 1);

    }

    public static void clear() {
        getInstance().userMap.clear();
        getInstance().accountMap.clear();
        getInstance().userStepsMap.clear();
    }
}
