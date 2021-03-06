package ru.otus.core.sql;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlGenerator<T> implements JdbcMapper {
    private static final String INSERT_QUERY = "INSERT INTO %s(%s) VALUES(%s)";
    private static final String SELECT_QUERY = "SELECT %s FROM %s WHERE %s= ?";
    private final Reflection<T> reflection;

    public SqlGenerator(Class<T> clazz) {
        reflection = new Reflection<>(clazz);
    }

    @Override
    public String getInsertQuery() {
        return String.format(
                INSERT_QUERY,
                getTableName(),
                concatFieldNames(reflection.getFieldNames().filter(getFilterForNotAutoIncrementFields()), Field::getName),
                concatFieldNames(reflection.getFieldNames().filter(getFilterForNotAutoIncrementFields()), f -> "?"));
    }

    @Override
    public String getSelectQuery() {
        return String.format(
                SELECT_QUERY,
                concatFieldNames(reflection.getFieldNames(), Field::getName),
                getTableName(),
                concatFieldNames(reflection.getFieldNames().filter(getFilterForUniqueField()), Field::getName));
    }

    private String getTableName() {
        return reflection.getObjectName().toLowerCase();
    }

    private String concatFieldNames(Stream<Field> stream, Function<? super Field, ? extends String> mapper) {
        return stream.map(mapper).collect(Collectors.joining(","));
    }

    private Predicate<? super Field> getFilterForNotAutoIncrementFields() {
        return field -> !field.isAnnotationPresent(Id.class);
    }

    private Predicate<? super Field> getFilterForUniqueField() {
        return field -> field.isAnnotationPresent(Id.class);
    }
}
