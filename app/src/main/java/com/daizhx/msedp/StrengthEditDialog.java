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
public class StrengthEditDialog extends EditDialog {
    public static String[] ss = new String[]{"30单位","29单位","28单位","27单位","26单位","25单位","24单位","23单位","22单位","21单位","20单位","19单位","18单位","17单位","16单位","15单位","14单位","13单位","12单位","11单位","10单位","9单位","8单位","7单位","6单位","5单位","4单位","3单位","2单位","1单位","0单位"};
    int value;
    interface SetStrengthListener{
        void setStrength(int value);
    }
    SetStrengthListener setStrengthListener;

    public void setSetStrengthListener(SetStrengthListener l){
        setStrengthListener = l;
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
        numberPicker.setMaxValue(30);
        numberPicker.setMinValue(0);
        numberPicker.setValue(value);
        numberPicker.setDisplayedValues(ss);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStrengthListener.setStrength(numberPicker.getValue());
                dismiss();
            }
        });
        setTitle("设置强度");
        return view;
    }
}
