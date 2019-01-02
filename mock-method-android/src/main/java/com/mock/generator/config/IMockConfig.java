package com.mock.generator.config;

import com.mock.generator.MockMethodModel;

import java.util.List;
import java.util.Map;

/**
 * Created by spf on 2019/1/2.
 */
public interface IMockConfig {

    boolean isEnable();

    Map<String,List<MockMethodModel>> getMockMap();

    void save();

    void saveModel(MockMethodModel mockMethodModel);
}
