package ru.kduskov.db.steps;

import ru.kduskov.db.DBRequest;
import ru.kduskov.db.models.Condition;
import ru.kduskov.db.models.dao.UserDao;

import java.util.List;

public class SqlSteps {
    public static List<UserDao> select() {
        // Предположим класс User с полями и аннотациями @Column где нужно
        return DBRequest.builder()
                .requestType(DBRequest.RequestType.SELECT)
                .table("users")
                .where(Condition.equalTo("role", "ADMIN"))
                .extractAs(UserDao.class);

    }
}
