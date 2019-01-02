package com.mock.generator.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mock.generator.BuildConfig;
import com.mock.generator.MockManager;
import com.mock.generator.MockMethodTransform;
import com.mock.generator.MockMethodModel;

import java.util.List;
import java.util.Map;

/**
 * Created by spf on 2018/12/24.
 */
public class DefaultMockConfig implements IMockConfig{
    private static SharedPreferences sharedPreferences;
    private final Application application;

    public DefaultMockConfig(Application application){
        this.application = application;
    }

    public static List<MockMethodModel> getMockItem() {
        return MockManager.getMockItem();
    }

    @Override
    public boolean isEnable() {
        return BuildConfig.DEBUG;
    }

    @Override
    public Map<String, List<MockMethodModel>> getMockMap() {

        sharedPreferences = application.getSharedPreferences("abTest", Context.MODE_PRIVATE);
        String abtest = sharedPreferences.getString("abtest", "");
        Gson gson = new Gson();
        if (TextUtils.isEmpty(abtest)) {
            Map<String, List<MockMethodModel>> mock = MockMethodTransform.getMock();
            abtest = gson.toJson(mock);
            sharedPreferences.edit().putString("abtest", abtest).apply();
        }

        Map<String, List<MockMethodModel>> map = gson.fromJson(abtest, new TypeToken<Map<String, List<MockMethodModel>>>() {
        }.getType());


        return map;
    }

    @Override
    public void save() {
        Map<String, List<MockMethodModel>> mockMap = MockManager.getMockMap();
        Gson gson = new Gson();
        sharedPreferences.edit().putString("abtest", gson.toJson(mockMap)).apply();
    }

    @Override
    public void saveModel(MockMethodModel mockMethodModel) {
        Map<String, List<MockMethodModel>> mockMap = MockManager.getMockMap();
        for (Map.Entry<String, List<MockMethodModel>> entry : mockMap.entrySet()) {
            if (entry.getKey().equals(mockMethodModel.className)){
                for (MockMethodModel model : entry.getValue()) {
                    if (model.methodName.equals(mockMethodModel.methodName)){
                        model.switcher = mockMethodModel.switcher;
                        model.defaultValue = mockMethodModel.defaultValue;
                    }
                }
            }
        }
        Gson gson = new Gson();
        sharedPreferences.edit().putString("abtest", gson.toJson(mockMap)).apply();
    }
}
