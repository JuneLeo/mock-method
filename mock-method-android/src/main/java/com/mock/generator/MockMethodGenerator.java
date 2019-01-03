package com.mock.generator;

import java.util.HashMap;
import java.util.List;

public class MockMethodGenerator implements IMockMethodMap {
  @Override
  public void initMackMock(List<HashMap<String, String>> mockMethodModels) {
//    mockMethodModels.add(getMockMap("com.mock.sample.ui.MainActivity","a","修改为a,修改为b","修改为a"));
    //todo plugin会注入代码
  }

  private HashMap<String, String> getMockMap(String className, String methodName, String values,
      String defaultValue) {
    HashMap<String,String> map = new HashMap<>();
    map.put("className",className);
    map.put("methodName",methodName);
    map.put("values",values);
    map.put("defaultValue",defaultValue);
    return map;
  }
}