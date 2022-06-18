package com.drace.sql.parser;

import com.drace.sql.wrapper.ResultWrapper;

public interface ObjectParser<T> {
    void parse(ResultWrapper resultWrapper, T t);

}
