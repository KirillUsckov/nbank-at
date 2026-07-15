package ru.kduskov.db.models.dao;

import lombok.*;
import ru.kduskov.api.enums.Role;

import java.util.List;

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
