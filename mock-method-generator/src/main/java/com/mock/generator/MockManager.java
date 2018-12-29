package com.mock.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by spf on 2018/12/29.
 */
public class MockManager {
    private static boolean isDebug = false;
    private static Map<String, List<MockMethodModel>> map;

    public static void setDebug(boolean isDebug) {
        MockManager.isDebug = isDebug;
    }

    public static void setMockMap(Map<String, List<MockMethodModel>> map) {
        MockManager.map = map;
    }

    public static Map<String, List<MockMethodModel>> getMockMap(){
        return map;
    }


    public static boolean isMock(String className, String methodName) {
        MockMethodModel model = getMockModel(className, methodName);
        return isDebug && model != null && model.switcher;
    }

    public static MockMethodModel getMockModel(String className, String methodName) {
        if (map == null) {
            return null;
        }
        List<MockMethodModel> mockMethodModels = map.get(className);
        if (mockMethodModels == null) {
            return null;
        }
        for (MockMethodModel mockMethodModel : mockMethodModels) {
            if (methodName.equals(mockMethodModel.methodName)) {
                return mockMethodModel;
            }
        }
        return null;
    }

    public static String getValue(String className, String methodName) {
        MockMethodModel model = getMockModel(className, methodName);
        if (model != null) {
            return model.defaultValue;
        }
        return "";
    }

    public static List<MockMethodModel> getMockItem() {
        List<MockMethodModel> list = new ArrayList<>();
        for (Map.Entry<String, List<MockMethodModel>> entry : map.entrySet()) {
            list.addAll(entry.getValue());
        }
        return list;
    }

}
