package ru.kduskov.db.models.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.kduskov.api.enums.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerDao extends BaseDao {
    private String username;
    private String password;
    private Role role;
    private String name;
}
