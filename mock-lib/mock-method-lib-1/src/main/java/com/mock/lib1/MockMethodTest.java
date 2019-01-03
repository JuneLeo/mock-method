package com.mock.lib1;

import com.mock.annotation.MockMethod;

/**
 * Created by spf on 2019/1/3.
 */
public class MockMethodTest {
    @MockMethod(defaultValue = "1", values = "1,2")
    public int test() {
        return 1;
    }

    @MockMethod(defaultValue = "true",values = "false,true")
    public boolean isEnable(){
        return false;
    }
}
