package com.mock.sample.abtest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mock.generator.MockManager;
import com.mock.generator.MockMethodManager;
import com.mock.generator.MockMethodModel;
import com.mock.sample.BuildConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by spf on 2018/12/24.
 */
public class ABTestMock {
    private static SharedPreferences sharedPreferences;

    public static void init(Application application) {
        sharedPreferences = application.getSharedPreferences("abTest", Context.MODE_PRIVATE);
        String abtest = sharedPreferences.getString("abtest", "");
        Gson gson = new Gson();
        if (TextUtils.isEmpty(abtest)) {
            Map<String, List<MockMethodModel>> mock = MockMethodManager.getMock();
            abtest = gson.toJson(mock);
            sharedPreferences.edit().putString("abtest", abtest).apply();
        }

        Map<String, List<MockMethodModel>> map = gson.fromJson(abtest, new TypeToken<Map<String, List<MockMethodModel>>>() {
        }.getType());
        MockManager.setMockMap(map);
        MockManager.setDebug(BuildConfig.DEBUG);
    }


    public static List<MockMethodModel> getMockItem() {
        return MockManager.getMockItem();
    }

    public static void saveData() {
        Map<String, List<MockMethodModel>> mockMap = MockManager.getMockMap();
        Gson gson = new Gson();
        sharedPreferences.edit().putString("abtest", gson.toJson(mockMap)).apply();
    }
}
