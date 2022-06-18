package com.drace.sql.connection.impl;

import com.drace.sql.connection.Connection;
import com.drace.sql.connection.ConnectionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.sql.DriverManager;

@RequiredArgsConstructor
public class SqliteConnection extends Connection {

    private java.sql.Connection connection;
    private final String database;

    public SqliteConnection connect() {

        try {

            Class.forName("org.sqlite.JDBC");

            File file = new File("plugins/" + database + ".db");

            if (!file.exists())
                file.createNewFile();

            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());

        }

        catch (Exception exception) { exception.printStackTrace(); }

        return this;

    }

    @Override
    public java.sql.Connection getConnection() {

        try { if (connection.isClosed()) connect(); }
        catch (Exception exception) { exception.printStackTrace(); }

        return connection;
    }

    public ConnectionType getType() { return ConnectionType.SQLITE; }

}
