package com.daizhx.msedp;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/20.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DeviceListDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    ListView listView;
    LeDeviceListAdapter listAdapter;
    ProgressBar progressBar;
    Button btn_refrsh;
//    List<Map<String,String>> data = new ArrayList<Map<String,String>>();
//    static final String[] from = new String[]{"name","addr"};

    private static DeviceListDialog deviceListDialog = new DeviceListDialog();

    public static synchronized DeviceListDialog getInstance(){
        return deviceListDialog;
    }

    interface OnItemClickListener{
        void onDeviceClick(String name,String addr);
        void onReScanRequest();
    }
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l){
        onItemClickListener = l;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_device_list,container);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        btn_refrsh = (Button) view.findViewById(R.id.btn_retry);
        listView = (ListView) view.findViewById(android.R.id.list);
//        listAdapter = new SimpleAdapter(getActivity(),data,android.R.layout.simple_list_item_2,from,new int[]{android.R.id.text1,android.R.id.text2});
        listAdapter = new LeDeviceListAdapter(getActivity());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        startScan();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(onItemClickListener != null){
            final BluetoothDevice device = listAdapter.getDevice(position);
            onItemClickListener.onDeviceClick(device.getName(),device.getAddress());
        }
    }

    public void addDevice(BluetoothDevice device){
        listAdapter.addDevice(device);
        listAdapter.notifyDataSetChanged();
    }
    public void clear(){
        listAdapter.clear();
    }

    public void startScan(){
        progressBar.setVisibility(View.VISIBLE);
        btn_refrsh.setVisibility(View.INVISIBLE);
        clear();
    }
    public void stopScan(){
        progressBar.setVisibility(View.INVISIBLE);
        btn_refrsh.setVisibility(View.VISIBLE);
        btn_refrsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onReScanRequest();
                    startScan();
                }
            }
        });
    }
}
