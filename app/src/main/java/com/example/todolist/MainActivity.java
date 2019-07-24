package com.example.todolist;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FragmentCalendar fragmentCalendar;
    FragmentToDoList fragmentToDoList;

    RelativeLayout lCalendar;
    RelativeLayout lList;

    ImageView ivCalendar;
    ImageView ivList;
    TextView tvCalendar;
    TextView tvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        lCalendar = findViewById(R.id.switch_calendar);
        lList = findViewById(R.id.switch_list);
        lCalendar.setOnClickListener(mClickListener);
        lList.setOnClickListener(mClickListener);

        ivCalendar = findViewById(R.id.iv_calendar);
        ivList = findViewById(R.id.iv_list);
        tvCalendar = findViewById(R.id.tv_calendar);
        tvList = findViewById(R.id.tv_list);

        fragmentCalendar = new FragmentCalendar(this);
        fragmentToDoList = new FragmentToDoList(this);
        setFragment(R.id.switch_calendar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    RelativeLayout.OnClickListener mClickListener = ((v)-> {
       switch (v.getId()) {
           case R.id.switch_calendar:
               setFragment(R.id.switch_calendar);
               break;
           case R.id.switch_list:
               setFragment(R.id.switch_list);
               break;
       }
    });

    public void setFragment(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (n) {
            case R.id.switch_calendar:
                ivCalendar.setImageResource(R.drawable.calendar_icon_p);
                tvCalendar.setTextColor(getColor(R.color.colorPrimary));
                ivList.setImageResource(R.drawable.list_icon);
                tvList.setTextColor(getColor(R.color.colorDark));
                fragmentTransaction.replace(R.id.main_frame, fragmentCalendar);
                fragmentTransaction.commit();
                break;
            case R.id.switch_list:
                ivCalendar.setImageResource(R.drawable.calendar_icon);
                tvCalendar.setTextColor(getColor(R.color.colorDark));
                ivList.setImageResource(R.drawable.list_icon_p);
                tvList.setTextColor(getColor(R.color.colorPrimary));
                fragmentTransaction.replace(R.id.main_frame, fragmentToDoList);
                fragmentTransaction.commit();
        }
    }


}
