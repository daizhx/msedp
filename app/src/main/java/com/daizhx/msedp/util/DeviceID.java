package com.daizhx.msedp.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.UUID;

/**
 * 获取android设备唯一标识符
 * 实现
 * 1，卸载重装保持一样
 * 2, 恢复出厂设置后保持一样
 * 4，重刷系统后保持一样
 */
public class DeviceID {

    //获取设备唯一标识符
    public static final String getDeviceID(Context context) {
        String deviceID = null;
//        deviceID = Base64.encodeToString(getDeviceMac(context).getBytes(),Base64.DEFAULT);
        return deviceID;
    }

    //获取mac地址

    /**
     * 获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，
     * 不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，
     * 这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。
     * @param context
     * @return
     */
    public static final String getDeviceMac(Context context) {
        String macAddress = null;

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        macAddress = wifiInfo.getMacAddress();

        Log.d("daizhx", "DeviceID mac = " + macAddress);
        return macAddress;
    }

    //通过jdk api获取mac地址,不需要权限声明，测试无法正常获取mac地址
    public static final String getDeviceMac2(Context context) {
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:03";
        }

        Log.d("daizhx", "DeviceID mac = " + macAddress);
        return macAddress;
    }

    //没有权限时无法获取
    public static final String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return "NO READ_PHONE_STATE PERMISSION";
            return null;
        }
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * 获取ANDROID_ID
     * 在设备首次运行的时候，系统会随机生成一64位的数字，
     * 并把这个数值以16进制保存下来，这个16进制的数字就是ANDROID_ID，
     * 但是如果手机恢复出厂设置这个值会发生改变。
     *
     * 注：有些设备是不会返回ANDROID_ID
     */
    public static final String getAndroidID(Context context) {
        String id = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }

    //获取设备序列号
    public static final String getSN(Context context) {
        String sn = null;
        if(Build.VERSION.SDK_INT < 26){
            sn = Build.SERIAL;
        }else{
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            sn = android.os.Build.getSerial();
        }
        if(Build.UNKNOWN.equals(sn)){
            sn = null;
        }
        return sn;
    }


    /**
     * 由于是与设备信息直接相关，如果是同一批次出厂的的设备有可能出现生成的内容可能是一样的。
     * （通过模拟器实验过，打开两个完全一样的模拟器，生成的内容是完全一下）
     * ，所以如果单独使用该方法也是不能用于生成唯一标识符的
     * @return
     */
    public static String getUniquePsuedoID() {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their phone, there will be a duplicate entry
        String serial = null;
        try
        {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
        catch (Exception e)
        {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 不依赖设备，APP构造UUID作为Device ID
     * 保存到外部存储的隐藏的文件中
     * 不足之处：
     * 1，必须依赖设备存在外部存储
     * 2，隐藏文件的方式不能保证不被删除，这个时候就不能成为设备唯一标识符
     */
    public static final String getUUID(Context context) {
        //TODO
        String deviceID = null;

        return deviceID;
    }

}
