package com.drace.sql.connection.query;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

public class Query {

    private final StringBuilder stringBuilder = new StringBuilder();
    @Getter private Object[] objects = new Object[0];

    @Deprecated
    public Query select(String[] fields) {

        stringBuilder.append("select ");

        return this;

    }

    public Query selectAll() {

        stringBuilder.append("select * ");

        return this;

    }

    public Query from(String table) {

        stringBuilder.append("from ")
                .append(table)
                .append(" ");

        return this;

    }

    public Query update(String table, String structure, Map<String, Object> values) {

        String[] split = structure.split(", ");
        Object[] objects = new Object[split.length];

        stringBuilder.append("update ")
                .append(table)
                .append(" set ");

        for (int index = 0; index < split.length; index++) {

            String string = split[index];
            objects[index] = values.get(string);

            stringBuilder.append(string)
                    .append(" = ?");

            if (index <= (split.length - 2)) {

                stringBuilder.append(", ");

            }
        }

        this.objects = objects;

        stringBuilder.append(" ");

        return this;

    }

    public Query create(String table, String structure) {

        stringBuilder.append("create table if not exists ")
                .append(table)
                .append(" (")
                .append(structure)
                .append(")");

        return this;

    }

    public Query where(String column, Object value) {

        objects = Arrays.copyOf(objects, objects.length + 1);

        objects[objects.length - 1] = value;

        stringBuilder.append("where ")
                .append(column)
                .append(" = ")
                .append("?");

        return this;

    }

    public Query delete() {

        stringBuilder.append("delete ");

        return this;

    }

    public Query and() {

        stringBuilder.append(" and  ");

        return this;

    }

    public Query insert() {

        stringBuilder.append("insert ");

        return this;

    }

    public Query into(String table, String structure) {

        stringBuilder.append("into ")
                .append(table)
                .append(" (")
                .append(structure)
                .append(") ");

        return this;

    }

    public Query values(Object[] objects) {

        this.objects = objects;
        stringBuilder.append("values (")
                .append("?")
                .append(StringUtils.repeat(", ?", objects.length - 1))
                .append(")");

        return this;

    }

    public Query engine(String engine) {

        stringBuilder.append(" ENGINE = ").append(engine);

        return this;

    }

    public String build() {

        return stringBuilder.toString();

    }

}
