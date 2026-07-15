package ru.kduskov.api.models.body.response.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.kduskov.api.enums.Role;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.models.dao.BaseDao;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseDao {
    private String username;
    private String password;
    private Role role;
    private List<AccountDao> accounts;
    private String name;
}
