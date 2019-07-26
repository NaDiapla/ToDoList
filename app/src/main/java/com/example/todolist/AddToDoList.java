package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;

public class AddToDoList extends AppCompatActivity {

    long id;
    LinearLayout layout;
    Toolbar tb;
    DatePicker datePicker;
    Date initDate;
    Date setDate;
    Calendar calendar = Calendar.getInstance();
    EditText title;
    EditText contents;

    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todolist);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);
        initDate = (Date)intent.getSerializableExtra("date");

        datePicker = findViewById(R.id.date_picker_add_list);
        layout = findViewById(R.id.add_todolist_layout);
        tb = findViewById(R.id.add_toolbar);
        setSupportActionBar(tb);

        title = findViewById(R.id.add_title);
        contents = findViewById(R.id.add_contents);

        calendar.setTime(initDate);
        setDate = new Date(calendar.getTimeInMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                (view, year, monthOfYear, dayOfMonth)->setDate(year, monthOfYear, dayOfMonth));

        realm = Realm.getDefaultInstance();
        if (id > 0) {
            RealmQuery<ToDoList> query = realm.where(ToDoList.class).equalTo("id", id);
            ToDoList results = query.findFirst();
            title.setText(results.getTitle());
            contents.setText(results.getContents());
        }

        //뒤로가기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:

                realm.executeTransaction((realm)-> {
                    if (id == 0) {   // Insert
                        Number currentID = realm.where(ToDoList.class).max("id");
                        long nextID;
                        if (currentID == null) {
                            nextID = 1;
                        } else {
                            nextID = currentID.longValue() + 1;
                        }
                        ToDoList toDoList = new ToDoList();
                        toDoList.setId(nextID);
                        toDoList.setTitle(title.getText().toString());
                        toDoList.setContents(contents.getText().toString());
                        toDoList.setModifyDate(setDate);
                        toDoList.setCheckDate(null);
                        toDoList.setCheckDone(false);
                        realm.insert(toDoList);
                    } else {   // Update
                        ToDoList toDoList = realm.where(ToDoList.class).equalTo("id", id).findFirst();
                        toDoList.setTitle(title.getText().toString());
                        toDoList.setContents(contents.getText().toString());
                        toDoList.setModifyDate(setDate);
                    }
                });
                realm.beginTransaction();
                realm.commitTransaction();
                finish();
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.setDate = new Date(cal.getTimeInMillis());
    }
}
