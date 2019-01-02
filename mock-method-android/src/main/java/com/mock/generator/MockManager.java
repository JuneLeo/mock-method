package com.mock.generator;

import com.mock.generator.config.IMockConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by spf on 2018/12/29.
 */
public class MockManager {
    private static boolean isEnable = false;
    private static Map<String, List<MockMethodModel>> map;
    private static IMockConfig mMockConfig;

    public static void init(IMockConfig iMockConfig) {
        mMockConfig = iMockConfig;
        MockManager.map = iMockConfig.getMockMap();
        isEnable = iMockConfig.isEnable();
    }

    ////////////////////
    // start  不能动
    ///////////////////
    /**
     * 是否可以mock
     * @param className
     * @param methodName
     * @return
     */
    public static boolean isMock(String className, String methodName) {
        MockMethodModel model = getMockModel(className, methodName);
        return isEnable && model != null && model.switcher;
    }

    /**
     * mock返回值
     * @param className
     * @param methodName
     * @return
     */
    public static String getValue(String className, String methodName) {
        MockMethodModel model = getMockModel(className, methodName);
        if (model != null) {
            return model.defaultValue;
        }
        return "";
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
    ///////////////
    //  end
    ///////////////

    /**
     * 获取Map
     * @return
     */
    public static Map<String, List<MockMethodModel>> getMockMap(){
        return map;
    }

    /**
     * 获取所有的Mock数据
     * @return
     */
    public static List<MockMethodModel> getMockItem() {
        List<MockMethodModel> list = new ArrayList<>();
        for (Map.Entry<String, List<MockMethodModel>> entry : map.entrySet()) {
            list.addAll(entry.getValue());
        }
        return list;
    }

    /**
     * 根据类名获取 类中方法
     * @param clz
     * @return
     */
    public static List<MockMethodModel> getMockItemByClz(String clz){
        for (Map.Entry<String, List<MockMethodModel>> entry : map.entrySet()) {
            if (entry.getKey().equals(clz)){
                return entry.getValue();
            }
        }
        return null;
    }

    public static IMockConfig getMockConfig(){
        return mMockConfig;
    }

}
