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
public class QiboEditDialog extends EditDialog {
    public static String[] ss = new String[]{"5次/分钟","6次/分钟","7次/分钟","8次/分钟","9次/分钟","10次/分钟","11次/分钟","12次/分钟","13次/分钟","14次/分钟","15次/分钟"};
    int value;
    interface SetQiboFreqListener{
        void setQiboFreqValue(int value);
    }
    SetQiboFreqListener setQiboFreqListener;

    public void setSetQiboFreqListener(SetQiboFreqListener l){
        setQiboFreqListener = l;
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
        numberPicker.setMinValue(5);
        numberPicker.setMaxValue(15);
        numberPicker.setValue(value);
        numberPicker.setDisplayedValues(ss);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQiboFreqListener.setQiboFreqValue(numberPicker.getValue());
                dismiss();
            }
        });
        setTitle("设置起搏频率");
        return view;
    }
}
