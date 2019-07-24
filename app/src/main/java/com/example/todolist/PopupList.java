package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class PopupList extends AppCompatActivity {

    Realm realm;
    RecyclerView recyclerView;
    ToDoListAdapter adapter;
    Calendar calendar;
    Date date;
    Date nextDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.popup_list);

        Intent intent = getIntent();
        date = (Date)intent.getSerializableExtra("date");
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        nextDate = new Date(calendar.getTimeInMillis());

        recyclerView = findViewById(R.id.rv_popup_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        realm = Realm.getDefaultInstance();
        setRecyclerView();

        realm.addChangeListener((realm)-> {
            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() == 0) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setRecyclerView() {
        RealmResults<ToDoList> toDoLists = realm.where(ToDoList.class)
                .greaterThanOrEqualTo("modifyDate", date)
                .lessThan("modifyDate", nextDate)
                .findAll()
                .sort("checkDone", Sort.ASCENDING, "id", Sort.ASCENDING);
        adapter = new ToDoListAdapter(toDoLists);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }
}
