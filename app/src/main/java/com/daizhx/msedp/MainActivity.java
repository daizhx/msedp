package com.daizhx.msedp;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity implements EditTimeDialog.SetListener,QiboEditDialog.SetQiboFreqListener
        ,McEditDialog.SetMcFreqListener,StrengthEditDialog.SetStrengthListener,DeviceListDialog.OnItemClickListener{

    String[] times = new String[]{"5分钟","10分钟","15分钟","20分钟","25分钟","30分钟"};

    ToggleButton btnOn;
    FrameLayout btnTimer;
    FrameLayout btnFreq;
    FrameLayout btnMc;
    FrameLayout btnStrength;

    TextView tvTime;
    TextView tvQibo;
    TextView tvMc;
    TextView tvStrength;

//    private View timerAnim;
//    private AnimationDrawable timeAnim;s

    Button btnConfirm;

    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;

    private boolean mScanning;
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    //    Chronometer timer;
    boolean timerTriggle = false;

    TextView tvNumber1;
    //倒计时数字
    TextView tvNumber2;
//    private ArrayList<BluetoothDevice> mLeDevices;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.d("daizhx","onLeScan-------->"+rssi+",device="+device.getName());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DeviceListDialog deviceListDialog = DeviceListDialog.getInstance();
                            deviceListDialog.addDevice(device);
                        }
                    });
                }

            };

//    ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            Log.d("daizhx","------------->"+result.toString());
//        }
//    };

    private Handler mHandler;

    private int mConnectState = STATE_DISCONNECTED;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private static final String TAG = "daizhx";

    private BluetoothGattService mBluetoothGattService;
    private BluetoothGattCharacteristic mBluetoothGattCharaterisic;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        final Intent intent = new Intent();
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED){
                mConnectState = STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                //get my service when connected
//                mBluetoothGattService = gatt.getService(serviceUUID);
                connected();
                gatt.discoverServices();
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
//                mConnectState = STATE_DISCONNECTED;
                disConnected();
                Log.i(TAG, "Disconnected from GATT server.");

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                //get my service when connected
                mBluetoothGattService = gatt.getService(Config.serviceUUID);
                mBluetoothGattCharaterisic = mBluetoothGattService.getCharacteristic(Config.CharacteristicUUID);
                Log.d(TAG,"setting previously settings.....................");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPreviouslySetting();
                    }
                });

            }else{
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS){

            }else {
                Log.w(TAG,"read data fail");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            command = characteristic.getValue();
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.w(TAG,"write data success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setArgText();
                        saveArg();
                    }
                });
            }else{
                Log.w(TAG,"write data fail");
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i(TAG,"onCharacteristicChanged:");
        }
    }; 
    private BluetoothGatt mBluetoothGatt;
    private String mBluetoothAddress;

    private byte[] command;// = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    private static final int TIME = 1;
    private static final int QB = 2;
    private static final int MC = 3;
    private static final int ST = 4;


    private void setPreviouslySetting(){
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.setTitle("是否设定上次运行设定的参数？").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writeBytes(command);
            }
        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        alertDialog.show();
        */
        writeBytes(command);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArg();
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConnectState == STATE_DISCONNECTED){
                    Toast.makeText(MainActivity.this,"请先连接设备",Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = v.getId();
                switch (id){
                    case R.id.btn_time:
                        showEditTimeDialog();
                        break;
                    case R.id.btn_freq:
                        showQiboEditDialog();
                        break;
                    case R.id.btn_mc:
                        showMcEditDialog();
                        break;
                    case R.id.btn_strength:
                        showStrengthEditDialog();
                        break;
                    default:
                        break;
                }
            }
        };
        btnTimer.setOnClickListener(onClickListener);
        btnFreq.setOnClickListener(onClickListener);
        btnMc.setOnClickListener(onClickListener);
        btnStrength.setOnClickListener(onClickListener);

        tvTime = (TextView) findViewById(R.id.tv_time);
        tvQibo = (TextView) findViewById(R.id.tv_qibo);
        tvMc = (TextView) findViewById(R.id.tv_mc);
        tvStrength = (TextView) findViewById(R.id.tv_strength);

        tvNumber1 = (TextView) findViewById(R.id.number1);
        tvNumber2 = (TextView) findViewById(R.id.number2);
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        mHandler = new Handler();
        Log.d(TAG,"command initial = "+Arrays.toString(command));
    }

    private void initView(){
        btnTimer = (FrameLayout) findViewById(R.id.btn_time);
        btnFreq = (FrameLayout) findViewById(R.id.btn_freq);
        btnMc = (FrameLayout) findViewById(R.id.btn_mc);
        btnStrength = (FrameLayout) findViewById(R.id.btn_strength);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);

        if(btnConfirm != null){
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeBytes(command);
                }
            });
        }

//        timerAnim = findViewById(R.id.img_timer);
//        timeAnim = (AnimationDrawable) timerAnim.getBackground();

        ToggleButton onOffBtn = (ToggleButton) findViewById(R.id.btn_on_off);
        onOffBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //turn on
                    command[0] = 0x01;
                    writeBytes(command);
                }else{
                    //turn off
                    command[0] = 0x02;
                    writeBytes(command);
                }
            }
        });
    }

    void setArgText(){
        Log.d(TAG,"set arg command="+Arrays.toString(command));
        tvTime.setText(times[command[TIME] - 1]);
        tvQibo.setText(QiboEditDialog.ss[command[QB] - 5]);
        tvMc.setText(McEditDialog.ss[command[MC] - 1]);
        tvStrength.setText(StrengthEditDialog.ss[command[ST]]);
    }


    CountDownTimer timer = new CountDownTimer(30*60*1000,1000*60) {
        @Override
        public void onTick(long l) {
            long r = l/(1000*60);
            tvNumber2.setText(r+"");
        }

        @Override
        public void onFinish() {
            //TODO
        }
    };
    void initArg(){
        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);
        int qibo = sp.getInt("qibo",0x09);
        int time = sp.getInt("time",0x06);
        int mc = sp.getInt("mc",0x03);
        int strength = sp.getInt("st",0x14);
        command = new byte[5];
        command[QB] = (byte) qibo;
        command[TIME] = (byte) time;
        command[MC] = (byte) mc;
        command[ST] = (byte) strength;
    }

    void saveArg(){
        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);
        sp.edit().putInt("qibo",command[QB]).putInt("time",command[TIME]).putInt("mc",command[MC]).putInt("st",command[ST]).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_CANCELED){
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mConnectState == STATE_CONNECTED) {
            disconnect();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mConnectState == STATE_CONNECTED){

            AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage("正在连接外部设备，是否确定断开连接？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    }).setNegativeButton("后台运行", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            startActivity(intent);
                        }
                    }).create();
            alertDialog.show();
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_scan, menu);
        if(mConnectState == STATE_DISCONNECTED){
            menu.findItem(R.id.menu_state).setTitle(R.string.disconnected);
        }else if(mConnectState == STATE_CONNECTED){
            menu.findItem(R.id.menu_state).setTitle(R.string.connected);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_state){
            if(mConnectState == STATE_DISCONNECTED){
//                mLeDevices.clear();
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    showDeviceList();
                    scanLeDevice(true);
                }else {
                    if(!checkAndRequestPermission()){
                        showDeviceList();
                        scanLeDevice(true);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
//                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                        mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
//                    }else {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    }
                    DeviceListDialog deviceListDialog = (DeviceListDialog) getFragmentManager().findFragmentByTag("deviceDialog");
                    if(deviceListDialog != null) {
                        deviceListDialog.stopScan();
                    }
                    supportInvalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            //TODO should change single mode
            mScanning = true;

            Log.d("daizhx","------------->start scan");
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
//                ScanSettings.Builder builder = new ScanSettings.Builder();
//                ScanSettings scanSettings = builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
//                scanner.startScan(null, scanSettings,scanCallback);
//
//            }else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
//            }

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            DeviceListDialog deviceListDialog = (DeviceListDialog) getFragmentManager().findFragmentByTag("deviceDialog");
            deviceListDialog.stopScan();
        }
        supportInvalidateOptionsMenu();
    }

    void showDeviceList(){
        DeviceListDialog deviceListDialog = DeviceListDialog.getInstance();
        deviceListDialog.setOnItemClickListener(this);
        deviceListDialog.show(getFragmentManager(),"deviceDialog");
    }

    private boolean checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH_ADMIN},0);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                showDeviceList();
                scanLeDevice(true);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void showEditTimeDialog(){
        EditTimeDialog dialog = new EditTimeDialog();
        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);
        int value = sp.getInt("time",0);
        Bundle args = new Bundle();
        args.putInt("value",value);
        dialog.setArguments(args);
        dialog.setSetListener(this);
        dialog.show(getFragmentManager(),"editTime");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void showQiboEditDialog(){
        QiboEditDialog dialog = new QiboEditDialog();
        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);
        int value = sp.getInt("qibo",0);
        Bundle args = new Bundle();
        args.putInt("value",value);
        dialog.setArguments(args);
        dialog.setSetQiboFreqListener(this);
        dialog.show(getFragmentManager(),"editQibo");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void showMcEditDialog(){
        McEditDialog dialog = new McEditDialog();
        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);
        int value = sp.getInt("mc",0);
        Bundle args = new Bundle();
        args.putInt("value",value);
        dialog.setArguments(args);
        dialog.setSetMcFreqListener(this);
        dialog.show(getFragmentManager(),"editMc");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void showStrengthEditDialog(){
        StrengthEditDialog dialog = new StrengthEditDialog();
        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);
        int value = sp.getInt("st",0);
        Bundle args = new Bundle();
        args.putInt("value",value);
        dialog.setArguments(args);
        dialog.setSetStrengthListener(this);
        dialog.show(getFragmentManager(),"editStrength");
    }


    @Override
    public void setTime(int i) {
        command[0] = 0x03;
        command[1] = (byte) i;
        writeBytes(command);
//        tvTime.setText(times[i-1]);
        timerTriggle = true;

    }

    @Override
    public void setMcFreq(int value) {
        command[0] = 0x03;
        command[3] = (byte) value;
        writeBytes(command);
//        tvMc.setText(McEditDialog.ss[value - 1]);
    }

    @Override
    public void setQiboFreqValue(int value) {
        command[0] = 0x03;
        command[2] = (byte) value;
        writeBytes(command);
//        tvQibo.setText(QiboEditDialog.ss[value - 5]);
    }

    @Override
    public void setStrength(int value) {
        command[0] = 0x03;
        command[4] = (byte) value;
        writeBytes(command);
//        tvStrength.setText(StrengthEditDialog.ss[value]);
    }

    @Override
    public void onDeviceClick(String name, String addr) {
        if(mScanning){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        connect(addr);
    }

    @Override
    public void onReScanRequest() {
        scanLeDevice(true);
    }

    private void connected(){
        mConnectState = STATE_CONNECTED;
        supportInvalidateOptionsMenu();
        DeviceListDialog deviceListDialog = DeviceListDialog.getInstance();
        deviceListDialog.dismiss();
    }
    private void disConnected(){
        mConnectState = STATE_DISCONNECTED;
        supportInvalidateOptionsMenu();
    }

    public boolean connect(final String addr){
        if(mBluetoothAdapter == null || addr == null){
            Log.w(TAG, "BluetoothAdapter not initialize or unspecified address");
            return false;
        }

        if(mBluetoothGatt != null && mBluetoothAddress != null && mBluetoothAddress.equals(addr)){
            if(mBluetoothGatt.connect()){
                mConnectState = STATE_CONNECTING;
                return true;
            }else {
                return false;
            }
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(addr);
        if(device == null){
            Log.w(TAG,"device not found.unable connect");
            return false;
        }
        //not autoconnect after device dropped
        mBluetoothGatt = device.connectGatt(this,false,mGattCallback);
        mBluetoothAddress = addr;
        mConnectState = STATE_CONNECTING;
        return true;
    }

    private void disconnect(){
        if(mBluetoothGatt != null){
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    /**
     * 发送指令
     * @param bytes
     * @return
     */
    public boolean writeBytes(byte[] bytes){
        Log.i(TAG,"write command bytes:"+ Arrays.toString(bytes));
        if(mBluetoothGatt != null && mBluetoothGattCharaterisic != null) {
            mBluetoothGattCharaterisic.setValue(bytes);
            return mBluetoothGatt.writeCharacteristic(mBluetoothGattCharaterisic);
        }else{
            Log.w(TAG,"write data fail,BluetoothGatt is null or BluetoothGattCharaterisic is null");
            Toast.makeText(this,"设置失败",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
