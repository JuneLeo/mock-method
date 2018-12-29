package com.mock.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mock.generator.MockMethodModel;
import com.mock.sample.R;
import com.mock.sample.abtest.ABTestMock;

import java.util.List;

/**
 * Created by spf on 2018/12/29.
 */
public class MockSettingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        List<MockMethodModel> mockMethodModels = ABTestMock.getMockItem();
        ListView listView = findViewById(R.id.list);
        MockAdapter mockAdapter = new MockAdapter(mockMethodModels);
        listView.setAdapter(mockAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ABTestMock.saveData();
    }

    private class MockAdapter extends BaseAdapter {

        private List<MockMethodModel> mockMethodModels;

        public MockAdapter(List<MockMethodModel> mockMethodModels) {
            this.mockMethodModels = mockMethodModels;
        }

        @Override
        public int getCount() {
            return mockMethodModels == null ? 0 : mockMethodModels.size();
        }

        @Override
        public Object getItem(int position) {
            return mockMethodModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(MockSettingActivity.this).inflate(R.layout.item_mock, null);
            final MockMethodModel model = mockMethodModels.get(position);
            TextView tvClass = convertView.findViewById(R.id.tv_class);
            tvClass.setText("class:" + model.className);

            TextView tvMethod = convertView.findViewById(R.id.tv_method);
            tvMethod.setText("method:" + model.methodName);


            SwitchCompat switchCompat = convertView.findViewById(R.id.switcher);
            switchCompat.setChecked(model.switcher);

            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    model.switcher = isChecked;
                }
            });

            final String[] values = model.values.split(",");
            int index = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(model.defaultValue)) {
                    index = i;
                    break;
                }
            }
            Spinner spinner = convertView.findViewById(R.id.spinner);
            ArrayAdapter<Object> adapter = new ArrayAdapter<>(MockSettingActivity.this, R.layout.item_abtest_spinner, R.id.item_spinner);
            adapter.addAll(values);
            spinner.setAdapter(adapter);
            spinner.setSelection(index);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    model.defaultValue = values[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            return convertView;
        }
    }
}
