package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FragmentToDoList extends Fragment {

    Context context;

    Realm realm;
    ImageView ivAdd;
    RecyclerView recyclerView;
    ToDoListAdapter adapter;

    public FragmentToDoList(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.todolist, container, false);

        ivAdd = v.findViewById(R.id.btn_add_list);
        ivAdd.setOnClickListener(mClickListener);

        recyclerView = v.findViewById(R.id.rv_todolist);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        realm = Realm.getDefaultInstance();
        setRecyclerView();

        realm.addChangeListener((r)-> adapter.notifyDataSetChanged());

        return v;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }

    ImageView.OnClickListener mClickListener = (v)-> {
            Intent intent = new Intent(getActivity(), AddToDoList.class);
            intent.putExtra("date", new Date(System.currentTimeMillis()));
            startActivity(intent);
    };

    private void setRecyclerView() {
        RealmResults<ToDoList> toDoLists = realm.where(ToDoList.class)
                .findAll().sort("checkDone", Sort.ASCENDING, "modifyDate", Sort.ASCENDING);
        adapter = new ToDoListAdapter(toDoLists);
        //adapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }
}
