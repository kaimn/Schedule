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

import static android.R.attr.defaultValue;
import static com.example.schedule.R.id.class_room;
import static com.example.schedule.R.id.course_name;

public class ChangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        final EditText inputCourseName = (EditText) findViewById(course_name);
        final EditText inputTeacher = (EditText) findViewById(R.id.teacher_name);
        final EditText inputClassRoom = (EditText) findViewById(class_room);
        final EditText inputDay = (EditText) findViewById(R.id.week);
        final EditText inputStart = (EditText) findViewById(R.id.classes_begin);
        final EditText inputEnd = (EditText) findViewById(R.id.classes_ends);

        Button okButton = (Button) findViewById(R.id.button);

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
                    Toast.makeText(ChangeActivity.this, "基本课程信息未填写", Toast.LENGTH_SHORT).show();
                }
                else {
                    CourseSchedule course = new CourseSchedule(courseName, teacher, classRoom,
                            Integer.valueOf(dayCource), Integer.valueOf(start), Integer.valueOf(end));
                    Intent intent = new Intent(ChangeActivity.this, CourceScheduleActivity.class);
                    intent.putExtra("course", course);
                    setResult(0, intent);
                    finish();
                }
            }
        });

        Intent intent = getIntent();
        String course_name=intent.getStringExtra("Course_name");
        String teacher=intent.getStringExtra("Teacher");
        String class_room=intent.getStringExtra("Class_room");
        Integer day=intent.getIntExtra("Day",defaultValue);
        Integer start=intent.getIntExtra("Start",defaultValue);
        Integer end=intent.getIntExtra("End",defaultValue);

        inputCourseName.setText(course_name);
        inputTeacher.setText(teacher);
        inputClassRoom.setText(class_room);


        inputDay.setText(String.valueOf(day));
        inputStart.setText(String.valueOf(start));
        inputEnd.setText(String.valueOf(end));

    }
}
