package com.daizhx.msedp.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daizhx.msedp.R;

public class LabelEditText extends LinearLayout {

    private TextView textview;
    private String labelText;
    private String labelPosition;
    private int labelFontSize;
    private EditText editText;

    public LabelEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        int resId = attrs.getAttributeResourceValue(null, "labelPosition", 0);
        if(resId != 0) {
            //如果能够获取到资源ID， 那就通过资源ID获取资源中配置的字符串
            labelPosition = getResources().getString(resId);
        }else{
            labelPosition = attrs.getAttributeValue(null, "labelPosition");
        }
        Log.d("lablePositionValue", labelPosition);
        resId = attrs.getAttributeResourceValue(null, "labelText", 0);
        if( resId != 0 ) {
            labelText = getResources().getString(resId);
        }else{
            labelText = attrs.getAttributeValue(null, "labelText");
        }

        resId = attrs.getAttributeResourceValue(null, "labelFontSize", 0);
        if( resId != 0 ) {
            labelFontSize = getResources().getInteger(resId);
        }else{
            labelFontSize = attrs.getAttributeIntValue(null, "labelFontSize", 14);
        }
        LayoutInflater li;
        li = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        if("left".equals(labelPosition)) {
            li.inflate(R.layout.horizontal, this);
        }else{
            li.inflate(R.layout.vertical, this);
        }
        textview = (TextView)findViewById(R.id.textView);
        textview.setText( labelText );
        textview.setTextSize((float)labelFontSize);

        editText = (EditText) findViewById(R.id.editText);
    }

    public LabelEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public String getText(){
        return editText.getText().toString().trim();
    }

    public void setError(CharSequence error){
        editText.setError(error);
    }

}
