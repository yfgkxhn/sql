package com.drace.sql.connection;

import com.drace.sql.connection.impl.MySQLConnection;
import com.drace.sql.connection.impl.SqliteConnection;
import com.drace.sql.connection.query.Query;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public abstract class Connection {

    public abstract Connection connect();
    public abstract java.sql.Connection getConnection();
    public ConnectionType getType() { return ConnectionType.NONE; }

    public static Connection of(ConfigurationSection section) {

        ConnectionType connectionType = ConnectionType.valueOf(section.getString("type").toUpperCase());

        switch (connectionType) {

            case SQLITE:
                return new SqliteConnection(section.getString("database"));

            case MYSQL:
                return new MySQLConnection(section.getString("host"), section.getString("database"), section.getString("user"), section.getString("pass"));

        }

        return null;

    }

    public void close() {

        try { getConnection().close(); }
        catch (Exception exception) { exception.printStackTrace(); }

    }

    public void executeAsync(Query query) {

        CompletableFuture.supplyAsync(() -> execute(query));

    }

    public boolean execute(Query query) {

        try (PreparedStatement statement = getConnection().prepareStatement(query.build())) {

            for (int index = 0; index < query.getObjects().length; index++)
                statement.setObject(index + 1, query.getObjects()[index]);

            statement.execute();

        }

        catch (Exception exception) { exception.printStackTrace(); return false; }

        return true;

    }

    public boolean updateAsync(Query query) {

        try { return CompletableFuture.supplyAsync(() -> update(query)).get(); }
        catch (Exception exception) { exception.printStackTrace(); return false; }

    }


    public boolean update(Query query) {

        try (PreparedStatement statement = getConnection().prepareStatement(query.build())) {

            for (int index = 0; index < query.getObjects().length; index++)
                statement.setObject(index + 1, query.getObjects()[index]);

            statement.execute();

        }

        catch (Exception exception) { exception.printStackTrace(); return false; }

        return true;

    }

    public PreparedStatement prepare(String query) {

        try (PreparedStatement statement = getConnection().prepareStatement(query)) {

            return statement;

        }

        catch (Exception exception) { exception.printStackTrace(); }

        return null;

    }

}
