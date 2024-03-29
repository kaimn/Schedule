package com.example.schedule.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.example.schedule.R;
import com.example.schedule.app.FileUtil;
import com.example.schedule.database.DatabaseHelper;
import com.example.schedule.entity.CourseSchedule;

import java.io.File;
import java.util.ArrayList;

/**
 * @time: 2019/1/12
 */
public class OcrActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_GENERAL = 100;
    private Context mContext = OcrActivity.this;
    private String mToken;
    private TextView mTv_result;
    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "database.db", null, 1);


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 100:
                    mTv_result.setText(String.valueOf(msg.obj));

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);


        initView();
    }

    private void initView() {

        mTv_result = findViewById(R.id.tv_result);

        Button btn_select = findViewById(R.id.btn_select);


        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageUI();
            }
        });


    }

    private void getToken() {

        OCR.getInstance(mContext).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                final String token = result.getAccessToken();

                Log.e("MainActivity", "MainActivity onResult()" + token);

                mToken = token;
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
                error.printStackTrace();
            }
        }, getApplicationContext());

    }


    /**
     * 打开百度提供的UI选择图片
     */
    private void openImageUI() {

        if (TextUtils.isEmpty(this.mToken)) {
            getToken();
        }


        // 生成intent对象
        Intent intent = new Intent(OcrActivity.this, CameraActivity.class);

        // 设置临时存储
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(getApplication()).getAbsolutePath());


        startActivityForResult(intent, REQUEST_CODE_GENERAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取调用参数
        String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
        // 通过临时文件获取拍摄的图片
        String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
        // 判断拍摄类型（通用，身份证，银行卡等）
        if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
            // 判断是否是身份证正面
            if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                // 获取图片文件调用sdk数据接口，见数据接口说明
            }

            getData(filePath);
        }
    }


    public void getData(String filePath) {


        // 通用文字识别参数设置
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(filePath));

        final StringBuffer sb = new StringBuffer();

// 调用通用文字识别服务
        OCR.getInstance(this).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                // 调用成功，返回GeneralResult对象
                for (WordSimple wordSimple : result.getWordList()) {
                    // wordSimple不包含位置信息
                    WordSimple word = wordSimple;
                    sb.append(word.getWords());
                    sb.append("\n");
                }
                // json格式返回字符串
                //listener.onResult(result.getJsonRes());

                Log.e("MainActivity", "MainActivity onResult()    ::" + sb.toString());


                Message msg = Message.obtain();
                msg.what = 100;
                msg.obj = sb.toString();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                int errorCode = error.getErrorCode();
                Log.e("MainActivity", "MainActivity onError()    ::" + errorCode);

                error.printStackTrace();
            }
        });


    }

    //从数据库加载数据
//    private void findData() {
//        ArrayList<CourseSchedule> courseList = new ArrayList<>(); //课程列表
//        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from course where course_name = ?", null);
//        if (cursor.moveToFirst()) {
//            do {
//                courseList.add(new CourseSchedule(
//                        cursor.getString(cursor.getColumnIndex("course_name")),
//                        cursor.getString(cursor.getColumnIndex("teacher")),
//                        cursor.getString(cursor.getColumnIndex("class_room")),
//                        cursor.getInt(cursor.getColumnIndex("day")),
//                        cursor.getInt(cursor.getColumnIndex("start")),
//                        cursor.getInt(cursor.getColumnIndex("end"))));
//            } while(cursor.moveToNext());
//        }
//        cursor.close();
//        //使用从数据库读取出来的课程信息来加载课程表视图
//        for (CourseSchedule courseSchedule : courseList) {
//            createCourseView(courseSchedule);
//        }
//    }

}
