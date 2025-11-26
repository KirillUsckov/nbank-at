package ru.kduskov.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserSession {
    int usersNumber() default 1;
    int userForLogin() default 1;
    int accountsNumber() default 0;
    boolean isUi() default false;
}
