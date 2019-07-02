package com.example.schedule.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.ocr.ui.camera.CameraActivity;
import com.example.schedule.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.schedule.app.FileUtil;
import com.example.schedule.constant.HttpConstants;
import com.example.schedule.utils.SimpleHttpUtil;
import com.example.schedule.utils.ToastUtils;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
public class LoginActivty extends AppCompatActivity {
    private static final String REGISTER = "";
    private static final String REMEMBER = "记住密码";

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Stetho.initializeWithDefaults(this);

    }
    public void register(View view) {
        try {
            Intent it = new Intent(LoginActivty.this, RegisterActivity.class);
            startActivity(it);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }


    public void login(View view) {
        //登录功能
        new Thread(new Runnable() {
            @Override
            public void run() {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);
                String param = "username=" + username.getText().toString() +"&password="+password.getText().toString();
                String url = HttpConstants.SERVER_HOST + "NutritionMaster/servlet/LoginServlet";
                String result = SimpleHttpUtil.doPost(url,param);

                Log.d("======>",result);
                try {
                    Gson gson = new Gson();
                    Map<String, String> resultMapper = gson.fromJson(result, new TypeToken<Map<String,String>>(){}.getType());
                    String status =  resultMapper.get("status");
                    Looper.prepare();
                    if("success".equals(status)){
                        String userInfo = resultMapper.get("data");
                        sp = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("userInfo",userInfo);
                        editor.commit();
                        Intent it = new Intent(LoginActivty.this, CourceScheduleActivity.class);
                        startActivity(it);
                        ToastUtils.show(getApplicationContext(),"登录成功");
                        LoginActivty.this.finish();
                    }else{
                        ToastUtils.show(getApplicationContext(),"登录失败,请输入正确是用户名或密码");
                    }
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }
}
