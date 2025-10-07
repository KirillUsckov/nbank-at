package ru.kduskov.models.body.response;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kduskov.enums.Role;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long id;
    private String username;
    private String password;
    private String name;
    private Role role;
    @Nullable
    private List<Account> accounts;
}
