package com.drace.sql.connection.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StorableField {

    public String column();
    public boolean primaryKey() default false;
    public String type() default "varchar(32)";

}
