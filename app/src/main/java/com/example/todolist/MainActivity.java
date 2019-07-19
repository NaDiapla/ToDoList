package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    Realm realm;
    ImageView ivAdd;
    RecyclerView recyclerView;
    ToDoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivAdd = findViewById(R.id.btn_add);
        ivAdd.setOnClickListener(mClickListener);

        recyclerView = findViewById(R.id.rvTodolist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        realm = Realm.getDefaultInstance();
        setRecyclerView();

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    ImageView.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AddToDoList.class);
            startActivity(intent);
        }
    };

    private void setRecyclerView() {
        //ToDoListAdapter adapter = new ToDoListAdapter(realm.where(ToDoList.class).findAll());
        RealmResults<ToDoList> toDoLists = realm.where(ToDoList.class)
                .findAll().sort("checkDone", Sort.ASCENDING, "id", Sort.ASCENDING);
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
