package com.mock.sample.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mock.generator.MockManager;
import com.mock.generator.config.DefaultMockConfig;
import com.mock.generator.ui.MockSettingActivity;
import com.mock.sample.BuildConfig;
import com.mock.sample.R;
import com.mock.annotation.MockMethod;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MockManager.init(new DefaultMockConfig(getApplication()){
            @Override
            public boolean isEnable() {
                return BuildConfig.DEBUG;
            }
        });

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MockSettingActivity.class);
                startActivity(intent);
            }
        });
        ((TextView) findViewById(R.id.tv_a)).setText("a方法:"+a());
        ((TextView) findViewById(R.id.tv_a)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.tv_a)).setText("a方法:"+a());
            }
        });
    }

    @MockMethod(defaultValue = "修改为a", values = "修改为a,修改为b")
    public String a() {
        return "我是a方法的返回值 - 设置后点击我改变值";
    }

    @MockMethod(defaultValue = "3", values = "1,3,4,5")
    public Long b() {
        return 1L;
    }
}
