package com.mock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by spf on 2018/12/24.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD})
public @interface MockField {
    String value();
    int type();
}
