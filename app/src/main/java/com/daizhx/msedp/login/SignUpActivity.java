package com.daizhx.msedp.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;

import com.daizhx.msedp.R;
import com.daizhx.msedp.util.HttpClient;
import com.daizhx.msedp.view.LabelEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    LabelEditText etAccount;
    LabelEditText etPwd;

    LabelEditText etName;
    RadioButton rbMale;
    RadioButton rbFemale;

    LabelEditText etTel;
    LabelEditText etAddr;

    LabelEditText etSBK;
    LabelEditText etYY;

    Switch isSmoken;

    LabelEditText etSmokenYears;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        etName = (LabelEditText) findViewById(R.id.et_name);
        rbMale = (RadioButton) findViewById(R.id.rb_male);
        rbFemale = (RadioButton) findViewById(R.id.rb_female);
        etTel = (LabelEditText) findViewById(R.id.et_tel);
        etAddr = (LabelEditText) findViewById(R.id.et_addr);

        etSBK = (LabelEditText) findViewById(R.id.et_sbk);
        etYY = (LabelEditText) findViewById(R.id.et_yy);

        isSmoken = (Switch) findViewById(R.id.switch_smoke);
        etSmokenYears = (LabelEditText) findViewById(R.id.et_smoke_year);

    }

    private boolean isNull(String s){
        return s == null || "".equals(s);
    }

    //注册
    private void signUp() {
        String account = etAccount.getText().toString().trim();
        if(isNull(account)){
            etAccount.setError("账号不能为空");
            return;
        }

        String pwd = etPwd.getText().toString().trim();
        if(isNull(pwd)){
            etPwd.setError("密码不能空");
            return;
        }


        String name = etName.getText();
        int gender = rbMale.isChecked() ? 1 : 0;
        //TODO get birt day
        String tel = etTel.getText();
        if(tel == null || "".equals(tel)){
            etTel.setError("电话号码不能为空");
            return;
        }
        String addr = etAddr.getText();
        //社保卡号
        String sbk = etSBK.getText();
        //就诊医院
        String yy = etYY.getText();

        boolean smokenFlag = isSmoken.isChecked();
        //烟龄
        String smokenYear = etSmokenYears.getText();

        HttpClient httpClient = HttpClient.getInstance();

        try {
            JSONObject msg = new JSONObject();
            msg.put("tel",Long.parseLong(tel));
            msg.put("name",name);
            msg.put("gender",gender);
            msg.put("homeAddr",addr);
            msg.put("medicareNumbers",sbk);
            msg.put("visitingHospital",yy);
            msg.put("isSmoking",smokenFlag);
            msg.put("smokingYear",smokenYear);

            httpClient.post("user/sign_up", msg.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("daizhx","call fail");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("daizhx","ret-------->"+response.body().string());
                    Intent data = new Intent();
                    data.putExtra("tel",etTel.getText().toString().trim());
                    data.putExtra("account",etAccount.getText().toString().trim());
                    data.putExtra("pwd",etPwd.getText().toString().trim());
                    setResult(RESULT_OK,data);
                    finish();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
