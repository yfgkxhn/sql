package com.drace.sql.connection.impl;

import com.drace.sql.connection.Connection;
import com.drace.sql.connection.ConnectionType;
import lombok.RequiredArgsConstructor;

import java.sql.DriverManager;

@RequiredArgsConstructor
public class MySQLConnection extends Connection {

    private java.sql.Connection connection;
    private final String host, database, user, pass;

    public MySQLConnection connect() {

        try {

            Class.forName("com.mysql.jdbc.Connection");

            connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:3306/%s", host, database)
                    , user, pass);
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

    public ConnectionType getType() { return ConnectionType.MYSQL; }

}
