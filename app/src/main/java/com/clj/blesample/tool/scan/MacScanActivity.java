package com.clj.blesample.tool.scan;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.clj.blesample.R;
import com.clj.blesample.tool.BluetoothService;
import com.clj.blesample.tool.operation.OperationActivity;
import com.clj.fastble.data.ScanResult;

/*
* 扫描指定物理地址的设备,并连接
* */
public class MacScanActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MacScanActivity.class";

    private EditText et;
    private Button btn_start, btn_stop;
    private ImageView img_loading;
    private Animation operatingAnim;
    private ProgressDialog progressDialog;

    private BluetoothService mBluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mac_scan);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null)
            unbindService();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("搜索设备");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et = (EditText) findViewById(R.id.et);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        img_loading = (ImageView) findViewById(R.id.img_loading);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                String mac = et.getText().toString();
                if (TextUtils.isEmpty(mac)) {
                    Toast.makeText(this, "请先输入MAC地址", Toast.LENGTH_LONG).show();
                } else {
                    if (mBluetoothService == null) {
                        bindService();
                    } else {
                        Log.d(TAG, "onClick: " + "connect device by mac");
                        mBluetoothService.scanAndConnect5(mac);
                    }
                }
                break;

            case R.id.btn_stop:
                if (mBluetoothService != null) {
                    mBluetoothService.cancelScan();
                }
                break;
        }
    }

    //绑定蓝牙service
    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    //解绑蓝牙服务
    private void unbindService() {
        this.unbindService(mFhrSCon);
    }


    private ServiceConnection mFhrSCon = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setScanCallback(callback);
            Log.d(TAG, "onServiceConnected: " + "connect device by mac");
            mBluetoothService.scanAndConnect5(et.getText().toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    private BluetoothService.Callback callback = new BluetoothService.Callback() {
        @Override
        public void onStartScan() {
            //开始扫描
            img_loading.startAnimation(operatingAnim);
            btn_start.setEnabled(false);
            btn_stop.setVisibility(View.VISIBLE);
        }

        @Override
        public void onScanning(ScanResult result) {
            //扫描中
        }

        @Override
        public void onScanComplete() {
            //完成扫描
            img_loading.clearAnimation();
            btn_start.setEnabled(true);
            btn_stop.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onConnecting() {
            //正在连接
            progressDialog.show();
        }

        @Override
        public void onConnectFail() {
            //连接失败
            img_loading.clearAnimation();
            btn_start.setEnabled(true);
            btn_stop.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
            Toast.makeText(MacScanActivity.this, "连接失败", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDisConnected() {
            //断开连接
            img_loading.clearAnimation();
            btn_start.setEnabled(true);
            btn_stop.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
            Toast.makeText(MacScanActivity.this, "连接断开", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServicesDiscovered() {
            //连接设备
            progressDialog.dismiss();
            startActivity(new Intent(MacScanActivity.this, OperationActivity.class));
        }
    };

}
