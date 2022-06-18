package com.drace.sql.wrapper;

import lombok.SneakyThrows;

import java.sql.ResultSet;

public class ResultWrapper implements AutoCloseable {

    private ResultSet resultSet;

    @SneakyThrows
    public static ResultWrapper of(ResultSet resultSet) {

        ResultWrapper wrapper = new ResultWrapper();

        wrapper.resultSet = resultSet;

        return wrapper;

    }

    public Object getObject(String string) {

        try { return resultSet.getObject(string); }
        catch (Exception exception) { exception.printStackTrace(); }

        return null;

    }
    public String getString(String string) {

        try { return resultSet.getString(string); }
        catch (Exception exception) { exception.printStackTrace(); }

        return null;

    }

    public boolean hasNext() {

        try { return resultSet.next(); }
        catch (Exception exception) { exception.printStackTrace(); }

        return false;

    }

    public void close() {

        try { resultSet.close(); }

        catch (Exception ignored) {}

    }

    public double getDouble(String string) {

        try { return resultSet.getDouble(string); }
        catch (Exception exception) { exception.printStackTrace(); }

        return 0.0;

    }

    public long getLong(String string) {

        try { return resultSet.getLong(string); }
        catch (Exception exception) { exception.printStackTrace(); }

        return 0L;

    }

    public int getInt(String string) {

        try { return resultSet.getInt(string); }
        catch (Exception exception) { exception.printStackTrace(); }

        return 0;

    }

}
