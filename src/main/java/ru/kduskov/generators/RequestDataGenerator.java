package ru.kduskov.generators;

import net.datafaker.Faker;
import ru.kduskov.annotations.GeneratingRule;
import ru.kduskov.models.body.request.BaseRequest;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

public class RequestDataGenerator {
    private final Faker faker;
    private final Random random;

    public RequestDataGenerator() {
        this.faker = new Faker();
        this.random = new Random();
    }

    public RequestDataGenerator(Locale locale) {
        this.faker = new Faker(locale);
        this.random = new Random();
    }

    /**
     * Заполняет все поля объекта случайными данными
     */
    public <T extends BaseRequest> T generateFilledObject(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            fillFields(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    /**
     * Рекурсивно заполняет все поля объекта
     */
    private void fillFields(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();

        // Поднимаемся по иерархии наследования до BaseRequest
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                // Пропускаем статические поля
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                // Если поле уже заполнено - пропускаем
                if (field.get(obj) != null) {
                    continue;
                }

                Object value = generateValueForField(field);
                field.set(obj, value);
            }

            // Переходим к родительскому классу
            clazz = clazz.getSuperclass();
            if (clazz == BaseRequest.class) {
                break;
            }
        }
    }

    /**
     * Генерирует значение для конкретного поля
     */
    private Object generateValueForField(Field field) {
        // Проверяем наличие аннотации GeneratingRule
        GeneratingRule rule = field.getAnnotation(GeneratingRule.class);

        if (rule != null && !rule.regex().isEmpty()) {
            return generateFromRegex(rule.regex());
        }

        // Генерация на основе типа поля
        return generateByType(field.getType());
    }

    /**
     * Генерация значения по regex с помощью Data Faker
     */
    private String generateFromRegex(String regex) {
        try {
            // Data Faker умеет генерировать данные по regex
            return faker.expression("#{regexify '" + regex + "'}");
        } catch (Exception e) {
            // Fallback: простая генерация если Data Faker не справляется
            return generateSimpleRegex(regex);
        }
    }

    /**
     * Простая генерация для базовых regex паттернов
     */
    private String generateSimpleRegex(String regex) {
        // Упрощенные паттерны для часто используемых случаев
        if (regex.equals("[a-zA-Z0-9]{8,16}")) {
            return faker.regexify("[a-zA-Z0-9]{8,16}");
        } else if (regex.equals("[a-z]+")) {
            return faker.lorem().word().toLowerCase();
        } else if (regex.equals("[A-Z]+")) {
            return faker.lorem().word().toUpperCase();
        } else if (regex.equals("\\d+")) {
            return String.valueOf(faker.number().randomNumber(6, true));
        } else if (regex.equals("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            return faker.internet().emailAddress();
        }

        // Общий случай
        return faker.regexify(regex);
    }

    /**
     * Генерация значения на основе типа поля
     */
    private Object generateByType(Class<?> type) {
        if (type == String.class) {
            return faker.lorem().word();
        } else if (type == int.class || type == Integer.class) {
            return faker.number().numberBetween(1, 1000);
        } else if (type == long.class || type == Long.class) {
            return faker.number().randomNumber();
        } else if (type == double.class || type == Double.class) {
            return faker.number().randomDouble(2, 1, 1000);
        } else if (type == boolean.class || type == Boolean.class) {
            return faker.bool().bool();
        } else if (type == Date.class) {
            return faker.date().birthday();
        } else if (type.isEnum()) {
            return generateEnumValue(type);
        } else if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        } else if (Map.class.isAssignableFrom(type)) {
            return new HashMap<>();
        } else if (type.getPackage() != null &&
                type.getPackage().getName().startsWith("ru.kduskov")) {
            // Рекурсивная генерация для кастомных классов
            try {
                Object nestedInstance = type.getDeclaredConstructor().newInstance();
                fillFields(nestedInstance);
                return nestedInstance;
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Генерация случайного значения enum
     */
    private Object generateEnumValue(Class<?> enumClass) {
        Object[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants.length > 0) {
            return enumConstants[random.nextInt(enumConstants.length)];
        }
        return null;
    }
}