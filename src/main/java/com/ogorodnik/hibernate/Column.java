package com.ogorodnik.hibernate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    int id() default 0;

    String name() default "";

    double salary() default 0.0;
}
