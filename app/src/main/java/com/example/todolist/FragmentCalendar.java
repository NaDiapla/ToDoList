package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.todolist.decorators.OneDayDecorator;
import com.example.todolist.decorators.SaturdayDecorator;
import com.example.todolist.decorators.SundayDecorator;
import com.example.todolist.decorators.ToDoListDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class FragmentCalendar extends Fragment {

    Context context;
    MaterialCalendarView materialCalendarView;
    ImageView ivAdd;
    Realm realm;
    RealmResults<ToDoList> toDoLists;
    HashSet<CalendarDay> modifyDates;

    public FragmentCalendar(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.calendar, container, false);
        materialCalendarView = v.findViewById(R.id.calender);
        ivAdd = v.findViewById(R.id.btn_add_calendar);
        ivAdd.setOnClickListener(mClickListener);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS);

        modifyDates = new HashSet<>();
        realm = Realm.getDefaultInstance();
        toDoLists = realm
                .where(ToDoList.class)
                .equalTo("checkDone", false)
                .distinct("modifyDate")
                .findAll();
        for (ToDoList tDL : toDoLists) {
            modifyDates.add(CalendarDay.from(tDL.getModifyDate()));
        }
        realm.addChangeListener((r)-> setDecorator());

        DateFormatTitleFormatter dateFormatTitleFormatter = new DateFormatTitleFormatter(
                new SimpleDateFormat("yyyy년 MM월", Locale.getDefault())
        );
        materialCalendarView.setTitleFormatter(dateFormatTitleFormatter);

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator(),
                new ToDoListDecorator(modifyDates));

        materialCalendarView.setOnDateChangedListener((widget, date, selected)-> {
            Calendar calendar = date.getCalendar();
            calendar.add(Calendar.DATE,1 );
            Date nextDate = new Date(calendar.getTimeInMillis());

            realm = Realm.getDefaultInstance();
            long countList = realm.where(ToDoList.class)
                    .greaterThanOrEqualTo("modifyDate", date.getDate())
                    .lessThan("modifyDate", nextDate).count();
            if (countList > 0) {
                Intent intent = new Intent(context, PopupList.class);
                intent.putExtra("date", date.getDate());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), AddToDoList.class);
                intent.putExtra("date", date.getDate());
                startActivity(intent);
            }
            materialCalendarView.clearSelection();
        });

        return v;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }

    private void setDecorator() {
        HashSet<CalendarDay> days = new HashSet<>();
        for (ToDoList tDL : toDoLists) {
            days.add(CalendarDay.from(tDL.getModifyDate()));
        }
        materialCalendarView.removeDecorators();
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator(),
                new ToDoListDecorator(days)
        );
    }

    ImageView.OnClickListener mClickListener = (v)-> {
        Intent intent = new Intent(getActivity(), AddToDoList.class);
        intent.putExtra("date", new Date(System.currentTimeMillis()));
        startActivity(intent);
    };
}
