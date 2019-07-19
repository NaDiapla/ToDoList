package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;

public class AddToDoList extends AppCompatActivity {

    long id;
    LinearLayout layout;
    Toolbar tb;
    EditText title;
    EditText contents;

    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todolist);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        layout = findViewById(R.id.add_todolist_layout);
        tb = findViewById(R.id.add_toolbar);
        setSupportActionBar(tb);

        title = findViewById(R.id.add_title);
        contents = findViewById(R.id.add_contents);

        if (id > 0) {
            realm = Realm.getDefaultInstance();
            RealmQuery<ToDoList> query = realm.where(ToDoList.class).equalTo("id", id);
            ToDoList results = query.findFirst();
            title.setText(results.getTitle());
            contents.setText(results.getContents());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
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

                realm = Realm.getDefaultInstance();
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
                        toDoList.setModifyDate(new Date(System.currentTimeMillis()));
                        toDoList.setCheckDate(null);
                        toDoList.setCheckDone(false);
                        realm.insert(toDoList);
                    } else {   // Update
                        ToDoList toDoList = realm.where(ToDoList.class).equalTo("id", id).findFirst();
                        toDoList.setTitle(title.getText().toString());
                        toDoList.setContents(contents.getText().toString());
                        toDoList.setModifyDate(new Date(System.currentTimeMillis()));
                    }
                });
                realm.beginTransaction();
                realm.commitTransaction();
                realm.close();
                Snackbar.make(layout, getString(R.string.saving), Snackbar.LENGTH_SHORT).show();
                finish();
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
