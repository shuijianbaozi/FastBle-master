package com.clj.blesample.tool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.clj.blesample.R;
import com.clj.blesample.tool.other.UserActivity;
import com.clj.blesample.tool.scan.AnyScanActivity;
import com.clj.blesample.tool.scan.MacScanActivity;
import com.clj.blesample.tool.scan.NameFuzzyScanActivity;
import com.clj.blesample.tool.scan.NameScanActivity;
import com.clj.blesample.tool.scan.NamesFuzzyScanActivity;
import com.clj.blesample.tool.scan.NamesScanActivity;


/**
 * 可以作为测试工具
 * android ble 扫描端
 * 1.权限
 * 2.开启蓝牙
 * 3.扫描
 * 4.连接
 * 5.断开
 *
 */
public class MainActivity extends AppCompatActivity {

    private String[] modes = new String[]{
            "扫描所有设备，并显示",
            "扫描指定广播名的设备，并连接（唯一广播名）",
            "扫描指定广播名的设备，并连接（模糊广播名）",
            "扫描指定广播名的设备，并连接（多个广播名）",
            "扫描指定广播名的设备，并连接（模糊、多个广播名）",
            "扫描指定物理地址的设备，并连接"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.txt_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        ListView mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(new ConnectModeAdapter(this, modes));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://扫描所有设备,并显示
                        startActivity(new Intent(MainActivity.this, AnyScanActivity.class));
                        break;

                    case 1://扫描指定广播名的设备,并连接(唯一广播名)
                        startActivity(new Intent(MainActivity.this, NameScanActivity.class));
                        break;

                    case 2://扫描指定广播名的设备,并连接(模糊广播名)
                        startActivity(new Intent(MainActivity.this, NameFuzzyScanActivity.class));
                        break;

                    case 3://扫描指定广播名的设备并连接(多个广播名)
                        startActivity(new Intent(MainActivity.this, NamesScanActivity.class));
                        break;

                    case 4://扫描指定广播名的设备并连接(模糊,多个广播名)
                        startActivity(new Intent(MainActivity.this, NamesFuzzyScanActivity.class));
                        break;

                    case 5://扫描指定物理地址的设备,并连接
                        startActivity(new Intent(MainActivity.this, MacScanActivity.class));
                        break;
                }
            }
        });
    }

    //适配器
    private class ConnectModeAdapter extends BaseAdapter {

        private Context context;
        private String[] modes;

        ConnectModeAdapter(Context context, String[] modes) {
            this.context = context;
            this.modes = modes;
        }

        @Override
        public int getCount() {
            if (modes == null)
                return 0;
            return modes.length;
        }

        @Override
        public String getItem(int position) {
            if (modes == null)
                return null;
            return modes[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_connect_mode, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.txt_mode = (TextView) convertView.findViewById(R.id.txt_mode);
            }
            holder.txt_mode.setText(modes[position]);
            return convertView;
        }

        class ViewHolder {
            TextView txt_mode;
        }
    }
}
