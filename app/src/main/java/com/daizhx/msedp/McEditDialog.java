package com.daizhx.msedp;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/5/19.
 */
public class McEditDialog extends EditDialog {
    public static String[] ss = new String[]{"30赫兹","35赫兹","40赫兹","45赫兹","50赫兹"};
    int value;
    interface SetMcFreqListener{
        void setMcFreq(int value);
    }

    SetMcFreqListener setMcFreqListener;

    public void setSetMcFreqListener(SetMcFreqListener l){
        setMcFreqListener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        value = args.getInt("value");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5);
        numberPicker.setValue(value);
        numberPicker.setDisplayedValues(ss);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMcFreqListener.setMcFreq(numberPicker.getValue());
                dismiss();
            }
        });
        setTitle("设置脉冲频率");
        return view;
    }
}
