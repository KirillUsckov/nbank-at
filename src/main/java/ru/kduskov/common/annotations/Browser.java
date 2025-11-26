package ru.kduskov.common.annotations;

import ru.kduskov.common.enums.Browsers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Browser {
    Browsers[] value();
}
