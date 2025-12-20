package ru.kduskov.db.models.dao;

import lombok.*;
import ru.kduskov.api.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDao extends BaseDao {
    private String username;
    private String password;
    private Role role;
    private String name;
}
