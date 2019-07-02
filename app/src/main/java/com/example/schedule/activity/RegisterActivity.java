package com.example.schedule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedule.R;
import com.example.schedule.constant.HttpConstants;
import com.example.schedule.utils.SimpleHttpUtil;

import java.lang.reflect.TypeVariable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    private TextView reg_button;
    private  String userNametext = null;
    private String passwordtext = null;
    private String phonetext = null;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg_button = (TextView) findViewById(R.id.register);

        final EditText userName = (EditText) findViewById(R.id.editText3);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText phone = (EditText) findViewById(R.id.phone);


        reg_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                //注册功能
                userNametext = userName.getText().toString();
                passwordtext = password.getText().toString();
                phonetext = phone.getText().toString();
                if(userNametext.length() != 0 && passwordtext.length() != 0 && phonetext.length() != 0){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = SimpleHttpUtil.doPost(HttpConstants.SERVER_HOST + "NutritionMaster/servlet/RegisterScheduleServlet",
                                    "username=" + userNametext + "&password=" + passwordtext + "&phone=" + phonetext);
                            Looper.prepare();
                            if (result.equals("1")) {
                                Intent it = new Intent(RegisterActivity.this, LoginActivty.class);
                                startActivity(it);
                            } else {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }
                    }).start();
                }
                else {
                    Toast.makeText(getApplicationContext(), "用户名或密码或电话号码不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void backToLogin(View v) {
        Intent it = new Intent(RegisterActivity.this, LoginActivty.class);
        startActivity(it);
    }

}
