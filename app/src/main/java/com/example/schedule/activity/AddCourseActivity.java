package com.example.schedule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schedule.R;
import com.example.schedule.entity.CourseSchedule;

import static com.example.schedule.R.id.class_room;
import static com.example.schedule.R.id.course_name;


public class AddCourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);


        final EditText inputCourseName = (EditText) findViewById(course_name);
        final EditText inputTeacher = (EditText) findViewById(R.id.teacher_name);
        final EditText inputClassRoom = (EditText) findViewById(class_room);
        final EditText inputDay = (EditText) findViewById(R.id.week);
        final EditText inputStart = (EditText) findViewById(R.id.classes_begin);
        final EditText inputEnd = (EditText) findViewById(R.id.classes_ends);

        Button okButton = (Button) findViewById(R.id.button);
        Button noButton = (Button) findViewById(R.id.cancel);
        noButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(AddCourseActivity.this, CourceScheduleActivity.class);
                finish();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = inputCourseName.getText().toString();
                String teacher = inputTeacher.getText().toString();
                String classRoom = inputClassRoom.getText().toString();
                String dayCource = inputDay.getText().toString();
                String start = inputStart.getText().toString();
                String end = inputEnd.getText().toString();

                if (courseName.equals("") || dayCource.equals("") || start.equals("") || end.equals("")) {
                    Toast.makeText(AddCourseActivity.this, "基本课程信息未填写", Toast.LENGTH_SHORT).show();
                }
                else {
                    CourseSchedule courseSchedule = new CourseSchedule(courseName, teacher, classRoom,
                            Integer.valueOf(dayCource), Integer.valueOf(start), Integer.valueOf(end));
                    Intent intent = new Intent(AddCourseActivity.this, CourceScheduleActivity.class);
                    intent.putExtra("courseSchedule", courseSchedule);
                    setResult(1, intent);
                    finish();
                }
            }
        });




    }


}

