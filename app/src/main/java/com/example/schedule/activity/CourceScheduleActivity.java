package com.example.schedule.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedule.R;
import com.example.schedule.activity.ChangeActivity;
import com.example.schedule.database.DatabaseHelper;
import com.example.schedule.entity.Course;
import com.example.schedule.entity.CourseSchedule;
import com.example.schedule.net.HttpConnection;
import com.example.schedule.utils.SharePreferenceUntil;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class CourceScheduleActivity extends AppCompatActivity {

    //星期几
    private RelativeLayout day;

    private static final int REQUEST_LOGIN = 0;
    private static final int REQUEST_ADD = 1;
    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "database.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cource_schedule);
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        //工具条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button delete=(Button) findViewById(R.id.delete_courses);

        //从数据库读取数据
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_LOGIN:
                    try {
                        //等待提示
                        final ProgressDialog dialog = new ProgressDialog(this);
                        dialog.setTitle("加载课程中");
                        dialog.show();
//                        //加载数据
////                        mCourseService.getCourse(SharePreferenceUntil.loadDataFromFile(SharePreferenceUntil.KEY_USERNAME),
//                                new HttpConnection.HttpCallBack<List<Course>>() {
//                                    @Override
//                                    public void callback(List<Course> data) {
//                                        //清空原有数据
////                                        mStuCourseList.clear();
////                                        mStuCourseDao.removeAll();
////                                        //加载数据
////                                        mStuCourseList.addAll(data);
////                                        showCls();
//                                        dialog.dismiss();
//                                    }
//                                });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_ADD:
                    try {
                        CourseSchedule courseSchedule = (CourseSchedule) data.getSerializableExtra("courseSchedule");//获取从AddCourseActivity传过来的数据
                        createCourseView(courseSchedule);
                        //存储数据到数据库
                        saveData(courseSchedule);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
        if (requestCode == 1 && resultCode == 1 && data != null) {
            CourseSchedule courseSchedule = (CourseSchedule) data.getSerializableExtra("courseSchedule");//获取从AddCourseActivity传过来的数据
            //创建课程表左边视图(节数)
//            createLeftView();
            //创建课程表视图
            createCourseView(courseSchedule);
            //存储数据到数据库
            saveData(courseSchedule);
        }
    }

    //创建课程节数的卡片视图
    private void createLeftView() {
        int maxClassNumber = 0;//课程的最大节数
        int number = 1; //课程表左侧的当前节数
//        if (len > maxClassNumber) {
        LinearLayout classNumberLayout = (LinearLayout) findViewById(R.id.class_number_layout);
        View view;
        TextView text;
        for (int i = 0; i < 12; i++) {

            view = LayoutInflater.from(this).inflate(R.layout.left_view, null);
            //布局填充器，将目标布局填充到当前Activity中
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110,180);
            //LayoutParams类是用于子视图向父视图传达自己的意愿
            view.setLayoutParams(params);

            text = view.findViewById(R.id.class_number_text);
            text.setText("" + number++);
            classNumberLayout.addView(view);
        }
//        }
    }

    //创建课程的卡片视图
    private void createCourseView(final CourseSchedule courseSchedule) {
        int height = 180;
        int integer = courseSchedule.getDay();
        if ((integer < 1 && integer > 7) || courseSchedule.getStart() > courseSchedule.getEnd())
            Toast.makeText(this, "日期或时间格式错误", Toast.LENGTH_LONG).show();
        else {
            switch (integer) {
                case 1: day = (RelativeLayout) findViewById(R.id.monday);break;
                case 2: day = (RelativeLayout) findViewById(R.id.tuesday);break;
                case 3: day = (RelativeLayout) findViewById(R.id.wednesday);break;
                case 4: day = (RelativeLayout) findViewById(R.id.thursday);break;
                case 5: day = (RelativeLayout) findViewById(R.id.friday);break;
                case 6: day = (RelativeLayout) findViewById(R.id.saturday);break;
                case 7: day = (RelativeLayout) findViewById(R.id.weekday);break;
            }
            final View view = LayoutInflater.from(this).inflate(R.layout.course_card, null);
            //加载单个课程布局
            view.setY(height * (courseSchedule.getStart()-1));
            //设置开始高度,即第几节课开始
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT,(courseSchedule.getEnd()-courseSchedule.getStart()+1)*height - 8);
            //设置布局高度,即跨多少节课
            view.setLayoutParams(params);

            TextView text = view.findViewById(R.id.text_view);
            Button delete=view.findViewById(R.id.delete_courses);

            Button change=view.findViewById(R.id.change_courses);

            text.setText(courseSchedule.getCourseName() + "\n" + courseSchedule.getTeacher() + "\n" + courseSchedule.getClassRoom());
            //显示课程名
            day.addView(view);


            delete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    view.setVisibility(View.GONE);//先隐藏
                    day.removeView(view);//再移除课程视图
                    SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();
                    sqLiteDatabase.execSQL("delete from course where course_name = ?",
                            new String[] {courseSchedule.getCourseName()});

                }
            });



            change.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
                    Cursor cursor =
                            sqLiteDatabase.rawQuery("select * from course where course_name = ?",
                                    new String[] {courseSchedule.getCourseName()});
                    view.setVisibility(View.GONE);//先隐藏
                    day.removeView(view);//再移除课程视图


                    cursor.moveToFirst();
                    String course_name=cursor.getString(cursor.getColumnIndex("course_name"));
                    String teacher =    cursor.getString(cursor.getColumnIndex("teacher"));
                    String class_room=cursor.getString(cursor.getColumnIndex("class_room"));
                    Integer day=        cursor.getInt(cursor.getColumnIndex("day"));
                    Integer start=    cursor.getInt(cursor.getColumnIndex("start"));
                    Integer end=      cursor.getInt(cursor.getColumnIndex("end"));
                    cursor.close();


                    Intent intent = new Intent(CourceScheduleActivity.this, ChangeActivity.class);
                    intent.putExtra("Course_name",course_name);
                    intent.putExtra("Teacher",teacher);
                    intent.putExtra("Class_room",class_room);
                    intent.putExtra("Day",day);
                    intent.putExtra("Start",start);
                    intent.putExtra("End",end);

                    sqLiteDatabase.execSQL("delete from course where course_name = ?",
                            new String[] {courseSchedule.getCourseName()});

                    startActivityForResult(intent ,0);

                }
            });



        }
    }

    //保存数据到数据库
    private void saveData(CourseSchedule courseSchedule) {
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into course(course_name, teacher, class_room, day, start, end) " +
                "values(?, ?, ?, ?, ?, ?)", new String[] {courseSchedule.getCourseName(), courseSchedule.getTeacher(),
                courseSchedule.getClassRoom(), courseSchedule.getDay()+"", courseSchedule.getStart()+"", courseSchedule.getEnd()+""});
    }

    //从数据库加载数据
    private void loadData() {
        ArrayList<CourseSchedule> courseList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from course", null);
        if (cursor.moveToFirst()) {
            do {
                courseList.add(new CourseSchedule(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("start")),
                        cursor.getInt(cursor.getColumnIndex("end"))));
            } while(cursor.moveToNext());
        }
        cursor.close();
        createLeftView();
        //使用从数据库读取出来的课程信息来加载课程表视图
        for (CourseSchedule courseSchedule : courseList) {
            createCourseView(courseSchedule);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_get_course:
                Intent intent = new Intent(this, LoginCourceActivity.class);
                startActivityForResult(intent,REQUEST_LOGIN);
                break;
            case R.id.qiandao:
                Intent intent1 = new Intent(this,OcrActivity.class);
                startActivity(intent1);
                break;
            case R.id.change_cource:
                Intent intent2 = new Intent(this,AddCourseActivity.class);
                startActivityForResult(intent2,REQUEST_ADD);
                break;
        }
        return true;
    }
}