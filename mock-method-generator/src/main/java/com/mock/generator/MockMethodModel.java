package com.mock.generator;

/**
 * Created by spf on 2018/12/24.
 */
public class MockMethodModel {
    public String className;
    public String methodName;
    public String returnType;
    public String values;
    public String defaultValue;
    public boolean switcher;

    public MockMethodModel(String className, String methodName, String returnType, String values, String defaultValue) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.values = values;
        this.defaultValue = defaultValue;

    }

    public MockMethodModel() {
    }
}
