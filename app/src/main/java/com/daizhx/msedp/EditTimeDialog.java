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

import java.util.Set;

/**
 * Created by Administrator on 2016/5/19.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EditTimeDialog extends DialogFragment {

    NumberPicker numberPicker;
    String[] times = new String[]{"5分钟","10分钟","15分钟","20分钟","25分钟","30分钟"};
    Button btnConfirm;
    int value = 1;

    SetListener setListener;

    interface SetListener{
        void setTime(int i);
    }
    public void setSetListener(SetListener l){
        setListener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        value = args.getInt("value");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_edit_time,container);
        numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setDisplayedValues(times);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(6);
        numberPicker.setValue(value);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                value = newVal;
            }
        });
        btnConfirm = (Button) view.findViewById(R.id.btn_yes);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setListener.setTime(value);
                dismiss();
            }
        });
        return view;
    }
}
