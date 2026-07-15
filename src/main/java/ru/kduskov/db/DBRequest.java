package ru.kduskov.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kduskov.common.confs.Config;
import ru.kduskov.common.enums.ConfigParams;
import ru.kduskov.db.annotations.Column;
import ru.kduskov.db.enums.Operators;
import ru.kduskov.db.enums.RequestType;
import ru.kduskov.db.enums.Tables;
import ru.kduskov.db.models.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class DBRequest {
    private RequestType requestType;
    private String select;
    private Tables table;
    private List<Condition> conditions;
    private String orderBy;
    private Integer limit;
    private Integer offset;
    private Map<String, Object> setValues;
    private Map<String, Object> insertValues;
    private Class<?> extractAsClass;

    // Основные методы выполнения
    public <T> List<T> extractAs(Class<T> clazz) {
        return executeQuery(clazz, true);
    }

    public <T> Optional<T> extractAsSingle(Class<T> clazz) {
        var results = executeQuery(clazz, true);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public int execute() {
        return executeUpdate();
    }

    // Специализированные методы
    public List<Map> extractAsMap() {
        return executeQuery(Map.class, true);
    }

    public Optional<Map> extractAsSingleMap() {
        var results = executeQuery(Map.class, true);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // Private методы выполнения запросов
    private <T> List<T> executeQuery(Class<T> clazz, boolean limitApplied) {
        var sql = buildSQL();

        try (Connection connection = getConnection();
            var statement = connection.prepareStatement(sql)) {

            setParameters(statement);

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapResultSet(resultSet, clazz);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    private int executeUpdate() {
        var sql = buildSQL();

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database update failed", e);
        }
    }

    // Маппинг ResultSet
    private <T> List<T> mapResultSet(ResultSet resultSet, Class<T> clazz) throws SQLException {
        List<T> results = new ArrayList<>();
        var metaData = resultSet.getMetaData();
        var columnCount = metaData.getColumnCount();

        // Собираем информацию о колонках один раз
        var columnToFieldMap = createColumnToFieldMap(clazz, metaData);

        while (resultSet.next()) {
            var result = mapRow(resultSet, metaData, columnCount, clazz, columnToFieldMap);
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }

    private <T> T mapRow(ResultSet rs, ResultSetMetaData metaData, int columnCount,
                         Class<T> clazz, Map<String, String> columnToFieldMap) throws SQLException {

        // Для Map
        if (clazz == Map.class) {
            return clazz.cast(mapToMap(rs, metaData, columnCount));
        }

        // Для базовых типов
        if (isBasicType(clazz)) {
            return mapToBasicType(rs, clazz);
        }

        // Для массивов
        if (clazz == Object[].class) {
            return clazz.cast(mapToArray(rs, columnCount));
        }

        // Для Dao-классов и других пользовательских классов
        return mapToClass(rs, clazz, columnToFieldMap);
    }

    // Вспомогательные методы маппинга
    private Map<String, Object> mapToMap(ResultSet rs, ResultSetMetaData metaData, int columnCount)
            throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            var columnName = metaData.getColumnLabel(i);
            var value = rs.getObject(i);
            map.put(columnName, value);
        }
        return map;
    }

    private Object[] mapToArray(ResultSet rs, int columnCount) throws SQLException {
        Object[] array = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            array[i] = rs.getObject(i + 1);
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private <T> T mapToBasicType(ResultSet rs, Class<T> clazz) throws SQLException {
        var value = rs.getObject(1);

        if (value == null) {
            return null;
        }

        if (clazz == String.class) {
            return clazz.cast(value.toString());
        } else if (clazz == Integer.class || clazz == int.class) {
            return clazz.cast(((Number) value).intValue());
        } else if (clazz == Long.class || clazz == long.class) {
            return clazz.cast(((Number) value).longValue());
        } else if (clazz == Double.class || clazz == double.class) {
            return clazz.cast(((Number) value).doubleValue());
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            if (value instanceof Boolean) {
                return clazz.cast(value);
            }
            if (value instanceof Number) {
                return clazz.cast(((Number) value).intValue() != 0);
            }
            return clazz.cast(Boolean.parseBoolean(value.toString()));
        } else if (clazz == Float.class || clazz == float.class) {
            return clazz.cast(((Number) value).floatValue());
        }

        return clazz.cast(value);
    }

    private <T> T mapToClass(ResultSet rs, Class<T> clazz, Map<String, String> columnToFieldMap)
            throws SQLException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Map.Entry<String, String> entry : columnToFieldMap.entrySet()) {
                String columnName = entry.getKey();
                String fieldName = entry.getValue();

                try {
                    Object value = rs.getObject(columnName);
                    if (!rs.wasNull()) {
                        setFieldValue(instance, fieldName, value);
                    }
                } catch (SQLException e) {
                    // Колонка может отсутствовать в результате
                }
            }

            return instance;
        } catch (Exception e) {
            throw new SQLException("Failed to map to class " + clazz.getName(), e);
        }
    }

    private void setFieldValue(Object instance, String fieldName, Object value)
            throws IllegalAccessException, NoSuchFieldException {

        Class<?> clazz = instance.getClass();
        Field field = null;

        // Ищем поле в текущем классе и всех родительских
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        if (field != null) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            // Конвертация значения к типу поля
            Object convertedValue = convertValue(value, fieldType);
            field.set(instance, convertedValue);
        }
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isEnum()) {
            return convertToEnum(value, targetType);
        }

        if (targetType == LocalDate.class && value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        } else if (targetType == LocalDateTime.class && value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        } else if (targetType == java.util.Date.class) {
            if (value instanceof java.sql.Date) {
                return new java.util.Date(((java.sql.Date) value).getTime());
            } else if (value instanceof java.sql.Timestamp) {
                return new java.util.Date(((java.sql.Timestamp) value).getTime());
            }
        }

        return value;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object convertToEnum(Object value, Class<?> enumType) {
        if (value instanceof String) {
            try {
                return Enum.valueOf((Class<Enum>) enumType, (String) value);
            } catch (IllegalArgumentException e) {
                // Пробуем через name()
                for (Enum<?> enumConstant : ((Class<Enum>) enumType).getEnumConstants()) {
                    if (enumConstant.name().equalsIgnoreCase((String) value)) {
                        return enumConstant;
                    }
                }
            }
        } else if (value instanceof Number) {
            int ordinal = ((Number) value).intValue();
            Enum<?>[] constants = ((Class<Enum>) enumType).getEnumConstants();
            if (ordinal >= 0 && ordinal < constants.length) {
                return constants[ordinal];
            }
        }
        return null;
    }

    // Построение SQL запроса
    private String buildSQL() {
        StringBuilder sql = new StringBuilder();

        switch (requestType) {
            case SELECT:
                buildSelectSQL(sql);
                break;
            case INSERT:
                buildInsertSQL(sql);
                break;
            case UPDATE:
                buildUpdateSQL(sql);
                break;
            case DELETE:
                buildDeleteSQL(sql);
                break;
            default:
                throw new UnsupportedOperationException("Request type " + requestType + " not implemented");
        }

        return sql.toString();
    }

    private void buildSelectSQL(StringBuilder sql) {
        sql.append("SELECT ");

        if (select != null && !select.trim().isEmpty()) {
            sql.append(select);
        } else {
            sql.append("*");
        }

        sql.append(" FROM ").append(table.getTableName());
        appendWhereClause(sql);
        appendOrderBy(sql);
        appendLimitOffset(sql);
    }

    private void buildInsertSQL(StringBuilder sql) {
        sql.append("INSERT INTO ").append(table.getTableName());

        if (insertValues != null && !insertValues.isEmpty()) {
            sql.append(" (")
                    .append(String.join(", ", insertValues.keySet()))
                    .append(") VALUES (")
                    .append(insertValues.keySet().stream()
                            .map(k -> "?")
                            .collect(Collectors.joining(", ")))
                    .append(")");
        }
    }

    private void buildUpdateSQL(StringBuilder sql) {
        sql.append("UPDATE ").append(table.getTableName()).append(" SET ");

        if (setValues != null && !setValues.isEmpty()) {
            sql.append(setValues.keySet().stream()
                    .map(k -> k + " = ?")
                    .collect(Collectors.joining(", ")));
        }

        appendWhereClause(sql);
    }

    private void buildDeleteSQL(StringBuilder sql) {
        sql.append("DELETE FROM ").append(table.getTableName());
        appendWhereClause(sql);
    }

    private void appendWhereClause(StringBuilder sql) {
        if (conditions != null && !conditions.isEmpty() && requestType.supportsWhere()) {
            sql.append(" WHERE ");

            for (int i = 0; i < conditions.size(); i++) {
                if (i > 0)  {
                    sql.append(" AND ");
                }
                Condition condition = conditions.get(i);
                sql.append(buildCondition(condition));
            }
        }
    }

    private String buildCondition(Condition condition) {
        if (condition.getOperator() == Operators.IN && condition.getValues() != null) {
            String placeholders = String.join(", ",
                    Collections.nCopies(condition.getValues().size(), "?"));
            return condition.getColumn() + " IN (" + placeholders + ")";
        }

        String conditionStr = condition.getColumn() + " " + condition.getOperator().getSymbol();
        if (condition.getOperator().requiresValue()) {
            conditionStr += " ?";
        }
        return conditionStr;
    }

    private void appendOrderBy(StringBuilder sql) {
        if (orderBy != null && !orderBy.trim().isEmpty()) {
            sql.append(" ORDER BY ").append(orderBy);
        }
    }

    private void appendLimitOffset(StringBuilder sql) {
        if (limit != null && limit > 0) {
            sql.append(" LIMIT ").append(limit);
            if (offset != null && offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        }
    }

    // Установка параметров в PreparedStatement
    private void setParameters(PreparedStatement stmt) throws SQLException {
        int paramIndex = 1;

        // Параметры для INSERT
        if (requestType == RequestType.INSERT && insertValues != null) {
            for (Object value : insertValues.values()) {
                stmt.setObject(paramIndex++, value);
            }
        }

        // Параметры для UPDATE
        if (requestType == RequestType.UPDATE && setValues != null) {
            for (Object value : setValues.values()) {
                stmt.setObject(paramIndex++, value);
            }
        }

        // Параметры условий WHERE
        if (conditions != null) {
            for (Condition condition : conditions) {
                if (condition.getOperator().requiresValue()) {
                    if (condition.getOperator() == Operators.IN && condition.getValues() != null) {
                        for (Object value : condition.getValues()) {
                            stmt.setObject(paramIndex++, value);
                        }
                    } else if (condition.getValue() != null) {
                        stmt.setObject(paramIndex++, condition.getValue());
                    }
                }
            }
        }
    }

    // Вспомогательные методы
    private boolean isBasicType(Class<?> clazz) {
        return clazz == String.class
                || clazz == Integer.class
                || clazz == int.class
                || clazz == Long.class
                || clazz == long.class
                || clazz == Double.class
                || clazz == double.class
                || clazz == Boolean.class
                || clazz == boolean.class
                || clazz == Float.class
                || clazz == float.class;
    }

    private Map<String, String> createColumnToFieldMap(Class<?> clazz, ResultSetMetaData metaData)
            throws SQLException {
        Map<String, String> map = new HashMap<>();

        if (clazz == Map.class || isBasicType(clazz) || clazz == Object[].class) {
            return map;
        }

        // Собираем все поля класса и его родителей
        Set<Field> allFields = getAllFields(clazz);

        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i).toLowerCase();
            String fieldName = findMatchingFieldName(allFields, columnName);

            if (fieldName != null) {
                map.put(metaData.getColumnLabel(i), fieldName);
            } else {
                System.out.println("Warning: Column '" + columnName + "' not mapped in class " + clazz.getName());
            }
        }

        return map;
    }

    private Set<Field> getAllFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    private String findMatchingFieldName(Set<Field> fields, String columnName) {
        for (Field field : fields) {
            String fieldName = field.getName().toLowerCase();

            // Проверяем аннотацию @Column
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                if (columnAnnotation.ignore()) {
                    continue;
                }
                if (!columnAnnotation.name().isEmpty()
                        && columnAnnotation.name().equalsIgnoreCase(columnName)) {
                    return field.getName();
                }
            }

            // Проверяем прямое совпадение
            if (fieldName.equals(columnName)) {
                return field.getName();
            }

            // Проверяем snake_case -> camelCase
            if (fieldName.equals(columnName.replace("_", "")) ||
                    fieldName.replace("_", "").equals(columnName.replace("_", ""))) {
                return field.getName();
            }
        }
        return null;
    }

    // Подсчет записей
    public long count() {
        DBRequest countRequest = DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(table)
                .conditions(conditions)
                .select("COUNT(*)")
                .build();

        return countRequest.extractAsSingle(Long.class).orElse(0L);
    }

    // Batch операции
    public int[] executeBatch(List<Map<String, Object>> batchData) {
        if (requestType != RequestType.INSERT) {
            throw new IllegalStateException("Batch operations supported only for INSERT");
        }

        String sql = buildSQL();

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Map<String, Object> rowData : batchData) {
                int paramIndex = 1;
                for (Object value : rowData.values()) {
                    statement.setObject(paramIndex++, value);
                }
                statement.addBatch();
            }

            return statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Batch insert failed", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.getProperty(ConfigParams.DB_URL),
                Config.getProperty(ConfigParams.DB_USERNAME),
                Config.getProperty(ConfigParams.DB_PASSWORD)
        );
    }

    // Builder
    public static DBRequestBuilder builder() {
        return new DBRequestBuilder();
    }

    public static class DBRequestBuilder {
        private RequestType requestType;
        private String select;
        private Tables table;
        private List<Condition> conditions = new ArrayList<>();
        private String orderBy;
        private Integer limit;
        private Integer offset;
        private Map<String, Object> setValues = new LinkedHashMap<>();
        private Map<String, Object> insertValues = new LinkedHashMap<>();
        private Class<?> extractAsClass;

        public DBRequestBuilder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public DBRequestBuilder select(String select) {
            this.select = select;
            return this;
        }

        public DBRequestBuilder table(Tables table) {
            this.table = table;
            return this;
        }

        public DBRequestBuilder where(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public DBRequestBuilder where(List<Condition> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        public DBRequestBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public DBRequestBuilder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public DBRequestBuilder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public DBRequestBuilder set(String column, Object value) {
            this.setValues.put(column, value);
            return this;
        }

        public DBRequestBuilder set(Map<String, Object> values) {
            this.setValues.putAll(values);
            return this;
        }

        public DBRequestBuilder value(String column, Object value) {
            this.insertValues.put(column, value);
            return this;
        }

        public DBRequestBuilder values(Map<String, Object> values) {
            this.insertValues.putAll(values);
            return this;
        }

        public DBRequestBuilder conditions(List<Condition> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        public DBRequest build() {
            return new DBRequest(
                    requestType, select, table, conditions, orderBy,
                    limit, offset, setValues, insertValues, extractAsClass
            );
        }

        // Методы для немедленного выполнения
        public <T> List<T> extractAs(Class<T> clazz) {
            return build().extractAs(clazz);
        }

        public <T> Optional<T> extractAsSingle(Class<T> clazz) {
            return build().extractAsSingle(clazz);
        }

        public int execute() {
            return build().execute();
        }

        public long count() {
            return build().count();
        }
    }
}