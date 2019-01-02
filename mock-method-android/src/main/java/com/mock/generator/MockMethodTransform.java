package com.mock.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by spf on 2018/12/24.
 */
public class MockMethodTransform {
    private static List<HashMap<String,String>> classMock = new ArrayList<>();

    static {
        try {
            Class<?> aClass = Class.forName("com.mock.generator.MockMethodMap");
            IMockMethodMap iMockMethodMap = (IMockMethodMap) aClass.newInstance();
            iMockMethodMap.initMackMock(classMock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<HashMap<String,String>> getMockMap(){
        return classMock;
    }

    public static Map<String, List<MockMethodModel>> getMock() {
        Map<String, List<MockMethodModel>> hashMap = new HashMap<>();
        for (HashMap<String,String> map : classMock) {
            String className = map.get("className");
            List<MockMethodModel> mockMethodModels = hashMap.get(className);

            if (mockMethodModels == null) {
                mockMethodModels = new ArrayList<>();
                hashMap.put(className, mockMethodModels);
            }
            MockMethodModel mockMethodModel = new MockMethodModel();
            mockMethodModel.className = className;
            mockMethodModel.methodName = map.get("methodName");
            mockMethodModel.returnType = map.get("returnType");
            mockMethodModel.values = map.get("values");
            mockMethodModel.defaultValue = map.get("defaultValue");
            mockMethodModels.add(mockMethodModel);
        }
        return hashMap;
    }

}
