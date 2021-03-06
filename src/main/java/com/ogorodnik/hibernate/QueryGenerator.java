package com.ogorodnik.hibernate;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class QueryGenerator<T, V> {
    private final static String WHERE_OPERATOR = " WHERE ";

    private String getTableName(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }
        return annotation.name().isEmpty() ? clazz.getName() : annotation.name();
    }

    private StringJoiner stringJoinerFields(Class<?> clazz) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ?
                        declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);
            }
        }
        return stringJoiner;
    }

    private StringBuilder preQueryGenerator(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        stringBuilder.append(stringJoinerFields(clazz));
        stringBuilder.append(" FROM ");
        stringBuilder.append(getTableName(clazz));
        return stringBuilder;
    }

    private String getId(Class<?> clazz) {
        String columnName;
        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                columnName = columnAnnotation.name().isEmpty() ?
                        declaredField.getName() : columnAnnotation.name();
                if (columnName.equals("id")) {
                    return columnName;
                } else {
                    throw new IllegalArgumentException("'id' column is missing");
                }
            }
        }
        return null;
    }

    public String getAll(Class<?> clazz) {
        StringBuilder stringBuilder = preQueryGenerator(clazz);
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    public String getById(Class<?> clazz, T id) {
        StringBuilder stringBuilder = preQueryGenerator(clazz);
        stringBuilder.append(WHERE_OPERATOR);
        stringBuilder.append(getId(clazz));
        stringBuilder.append(" = ").append(id).append(";");
        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, T id) {
        return "DELETE FROM " + getTableName(clazz) +
                WHERE_OPERATOR +
                getId(clazz) +
                " = " + id + ";";
    }

    public String insert(V value) throws IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
        Class<?> clazz = value.getClass();
        stringBuilder.append(getTableName(clazz));
        stringBuilder.append(" (");
        stringBuilder.append(stringJoinerFields(clazz));
        stringBuilder.append(") VALUES ('");
        StringJoiner stringJoiner = new StringJoiner("', '");
        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            Object field = declaredField.get(value);
            stringJoiner.add(field.toString());
        }
        stringBuilder.append(stringJoiner);
        stringBuilder.append("');");
        return stringBuilder.toString();
    }

    public String update(Object value) throws IllegalAccessException {
        String idName = null;
        Object idValue = null;
        StringBuilder stringBuilder = new StringBuilder("UPDATE ");
        Class<?> clazz = value.getClass();
        stringBuilder.append(getTableName(clazz));
        stringBuilder.append(" SET ");
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Field declaredField : clazz.getDeclaredFields()) {
            StringBuilder tmp = new StringBuilder();
            declaredField.setAccessible(true);
            Object field = declaredField.get(value);
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ?
                        declaredField.getName() : columnAnnotation.name();
                if ("id".equals(columnName)) {
                    idName = columnName;
                    idValue = field;
                } else {
                    stringJoiner.add(tmp.append(columnName).append(" = '").append(field).append("'"));
                }
            }
        }
        stringBuilder.append(stringJoiner);
        if (idName == null) {
            throw new IllegalArgumentException("'id' column is missing");
        }
        stringBuilder.append(WHERE_OPERATOR).append(idName).append(" = ").append(idValue);
        return stringBuilder.toString();
    }
}
