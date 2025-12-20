package ru.kduskov.db.enums;

import lombok.ToString;

/**
 * Типы SQL-запросов, используемые в DbRequest.
 */
@ToString
public enum RequestType {
    /** Выборка данных */
    SELECT,
    /** Вставка новой записи */
    INSERT,
    /** Обновление существующих записей */
    UPDATE,
    /** Удаление записей */
    DELETE;

    /**
     * Возвращает true, если для данного типа запроса применимы WHERE-условия.
     */
    public boolean supportsWhere() {
        switch (this) {
            case SELECT:
            case UPDATE:
            case DELETE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Возвращает true, если данный тип запроса обычно использует VALUES/SET (т.е. тело с данными).
     */
    public boolean requiresValues() {
        switch (this) {
            case INSERT:
            case UPDATE:
                return true;
            default:
                return false;
        }
    }
}

