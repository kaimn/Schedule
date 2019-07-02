package com.example.schedule.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.schedule.R;
import com.example.schedule.app.BaseCourerActivity;
import com.example.schedule.net.HttpConnection;
import com.example.schedule.net.LoginService;
import com.example.schedule.utils.SharePreferenceUntil;
import com.example.schedule.utils.ToastUtils;

import java.io.IOException;

public class LoginCourceActivity extends BaseCourerActivity {

    private EditText mEtUserName;
    private EditText mEtPwd;
    private EditText mEtCodes;
    private ImageView mIvCodesIcon;
    private Button mBtnLogin;

    private LoginService mLoginService;
    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_cource);

        mEtUserName = getViewById(R.id.login_et_username);
        mEtPwd = getViewById(R.id.login_et_pwd);
        mEtCodes = getViewById(R.id.login_et_codes);
        mIvCodesIcon = getViewById(R.id.login_iv_codes_img);
        mBtnLogin = getViewById(R.id.login_btn_login);
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        mLoginService = new LoginService(this);
        //获取存储的账号和密码
        String userName = SharePreferenceUntil.loadDataFromFile(SharePreferenceUntil.KEY_USERNAME);
        String pwd = SharePreferenceUntil.loadDataFromFile(SharePreferenceUntil.KEY_PWD);
        if (!userName.equals("") && !pwd.equals("")){
            mEtUserName.setText(userName);
            mEtPwd.setText(pwd);
        }
        //初始化验证码
        setUpCodesImage();
    }

    private void setUpCodesImage(){
        try {
            mLoginService.getCodesImg(new HttpConnection.HttpCallBack<Bitmap>() {
                @Override
                public void callback(Bitmap data) {
                    mIvCodesIcon.setImageBitmap(data);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initClick() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置正在登陆
                final ProgressDialog dialog = new ProgressDialog(LoginCourceActivity.this);
                dialog.show();
                //获取登陆数据，并验证
                final String userName = mEtUserName.getText().toString().trim();
                final String pwd = mEtPwd.getText().toString().trim();
                String codes = mEtCodes.getText().toString().trim();

                try {
                    mLoginService.login(userName, pwd, codes, new HttpConnection.HttpCallBack<Boolean>() {
                        @Override
                        public void callback(Boolean data) {
                            dialog.dismiss();
                            //登陆成功
                            if (data){
                                //存储数据
                                SharePreferenceUntil.saveData2File(SharePreferenceUntil.KEY_USERNAME,userName);
                                SharePreferenceUntil.saveData2File(SharePreferenceUntil.KEY_PWD,pwd);
                                SharePreferenceUntil.saveData2File(SharePreferenceUntil.KEY_STATE,"1");

                                //跳转到课表界面（因为是从课表界面调到登陆界面的，所以将自己杀死就可以了）
                                setResult(RESULT_OK);
                                finish();
                            }
                            else {
                                //提示账号或者密码错误
                                ToastUtils.makeText("账号或密码错误", Toast.LENGTH_SHORT);
                                //重新获取验证码
                                setUpCodesImage();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //重新设置验证码
        mIvCodesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpCodesImage();
            }
        });
    }

    @Override
    protected void processLogin(Bundle savedInstanceState) {

    }
}
