package com.mock.sample;

import com.mock.annotation.MockMethod;

/**
 * Created by spf on 2018/12/16.
 */
public class A {
    private static boolean isDebug = BuildConfig.DEBUG;

    @MockMethod(defaultValue = "1", values = "1,2")
    private String getA() {
        return "";
    }

    @MockMethod(defaultValue = "1", values = "1,2")
    private Float F() {
        return 1f;
    }

    @MockMethod(defaultValue = "1", values = "1,2")
    private float f() {
        return 1f;
    }


}
