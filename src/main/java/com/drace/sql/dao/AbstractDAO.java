package com.drace.sql.dao;

import com.drace.shared.ObjectUtils;
import com.drace.sql.connection.Connection;
import com.drace.sql.connection.ConnectionType;
import com.drace.sql.connection.annotations.StorableField;
import com.drace.sql.connection.annotations.StorableObject;
import com.drace.sql.connection.query.Query;
import com.drace.sql.parser.ObjectParser;
import com.drace.sql.wrapper.ResultWrapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractDAO<T> {

    private final Connection connection;
    /* internal parser method*/ private final ObjectParser<T> objectParser;
    /* internal strings */ private final String structure, table;

    /**
     * constructor used to instantiate new DAO
     * this will automatically create the table into db
     * @param conn
     * @param objectParser
     */
    public AbstractDAO(Connection conn, ObjectParser<T> objectParser) {

        this.connection = conn;
        this.objectParser = objectParser;

        T t = empty();
        StorableObject storableObject = t.getClass().getAnnotation(StorableObject.class);
        StringBuilder stringBuilder = new StringBuilder(),
                    stringBuilder1 = new StringBuilder();
        Field[] fields = Arrays.stream(t.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(StorableField.class))
                .toArray(Field[]::new);

        try {

            for (int index = 0; index < fields.length; index++) {

                Field field = fields[index];

                if (!field.isAnnotationPresent(StorableField.class)) continue;

                StorableField storableField = field.getAnnotation(StorableField.class);

                stringBuilder.append(storableField.column());
                stringBuilder1.append(storableField.column())
                        .append(" ")
                        .append(storableField.type());

                if (index <= (fields.length - 2)) {

                    stringBuilder.append(", ");
                    stringBuilder1.append(", ");

                }

            }
        }

        catch (Exception exception) { exception.printStackTrace(); }

        structure = stringBuilder.toString();
        table = storableObject.table();

        Query query = new Query().create(table, stringBuilder1.toString());

        if (connection.getType() == ConnectionType.MYSQL)
            query = query.engine("MyISAM");

        connection.execute(query);

    }

    /**
     * selects and parse all data from db to T instances
     * @return Set containing all T instances
     */
    public Set<T> selectAll() {

        T EMPTY = empty();
        T t;
        Set<T> data = new LinkedHashSet<>();

        try (ResultSet resultSet = connection.getConnection().prepareStatement("select * from " + table).executeQuery()) {

            while (resultSet.next()) {

                t = empty();

                objectParser.parse(ResultWrapper.of(resultSet), t);

                data.add(t);

            }

        }

        catch (Exception exception) { exception.printStackTrace(); }

        return data;

    }

    /**
     * retrieve object data from db
     * @param string identifier value
     * @return data from identifier parsed to T object.
     */
    public Supplier<T> selectOne(Object string) {

        T t = empty();
        StorableObject storableObject = t.getClass().getAnnotation(StorableObject.class);

        try (PreparedStatement statement = connection.prepare(
                String.format("select * from %s where %s = ?", table, storableObject.identifer()))) {

            statement.setObject(1, string);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    objectParser.parse(ResultWrapper.of(resultSet), t);

                }
            }
        }

        catch (Exception exception) { exception.printStackTrace(); }

        return () -> t.equals(empty()) ? null : t;

    }

    public void updateOne(T t) {

        CompletableFuture.runAsync(() -> updateOneSync(t));

    }


    public void updateOneSync(T t) {

        StorableObject storableObject = t.getClass().getAnnotation(StorableObject.class);
        Map<String, Object> objectMap = Maps.newHashMap();

        for (Field field : t.getClass().getDeclaredFields()) {

            if (!field.isAnnotationPresent(StorableField.class))
                continue;

            Object object = null;

            try { object = FieldUtils.readField(field, t, true); }
            catch (IllegalAccessException exception) { exception.printStackTrace(); }

            if (object instanceof Collection || object instanceof Map)
                object = ObjectUtils.serializeObject(object);

            objectMap.put(field.getName(), object);

        }

        connection.update(new Query().update(table, structure, objectMap).where(storableObject.identifer(),
                objectMap.get(storableObject.identifer())));

    }

    /**
     * insert object into db from other thread
     * @param t
     */
    public void insertOne(T t) {

        CompletableFuture.runAsync(() -> insertOneSync(t));

    }

    /**
     * insert object into db from current thread
     * @param t
     */
    public void insertOneSync(T t) {

        Object[] objects = Arrays.stream(t.getClass().getDeclaredFields()).filter(filter -> filter.isAnnotationPresent(StorableField.class)).map(field -> {

            Object object = null;

            try { object = FieldUtils.readField(field, t, true); }
            catch (IllegalAccessException exception) { exception.printStackTrace(); return null; }

            if (object instanceof Collection || object instanceof Map)
                object = ObjectUtils.serializeObject(object);

            return object;

        }).toArray(Object[]::new);

        connection.execute(new Query()
                .insert()
                .into(table, structure)
                .values(objects));

    }

    /**
     * removes a reference to object in db using another thread
     * @param t
     */
    public void deleteOne(T t) {

        CompletableFuture.runAsync(() -> deleteOneSync(t));

    }

    /**
     * removes a reference to object in db using current thread
     * @param t
     */
    public void deleteOneSync(T t) {

        try {
            StorableObject storableObject = t.getClass().getAnnotation(StorableObject.class);
            Object identifier = FieldUtils.readDeclaredField(t, storableObject.identifer(), true);

            connection.execute(new Query()
                    .delete()
                    .from(table)
                    .where(storableObject.identifer(), identifier));
        }

        catch (Exception exception) { exception.printStackTrace(); }

    }

    /**
     * abstract method used to generate new T instances
     * @return new empty instance of T
     */
    public abstract T empty();

}
