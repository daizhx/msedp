package com.daizhx.msedp;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/5/19.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EditDialog extends DialogFragment {
    NumberPicker numberPicker;
    Button btnConfirm;
    TextView tvTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_edit_time,container);
        numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        btnConfirm = (Button) view.findViewById(R.id.btn_yes);
        tvTitle = (TextView) view.findViewById(R.id.title);
        return view;
    }

    void setTitle(String s){
        tvTitle.setText(s);
    }

    void setTitle(int sid){
        tvTitle.setText(sid);
    }
}
