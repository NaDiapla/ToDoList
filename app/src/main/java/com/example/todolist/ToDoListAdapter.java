package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ToDoListAdapter extends RealmRecyclerViewAdapter<ToDoList, ToDoListAdapter.ViewHolder>
        implements ItemActionListener {

    //static final int SWIPE_LEFT = 16;
    //static final int SWIPE_RIGHT = 32;

    Realm realm;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView toDoListTitle;
        TextView toDoListContents;
        TextView toDoListModifyDate;
        TextView toDoListCheckDate;
        View layout;

        ViewHolder(View itemView) {
            super(itemView);
            toDoListTitle = itemView.findViewById(R.id.title);
            toDoListTitle.setTypeface(null, Typeface.BOLD);
            toDoListContents = itemView.findViewById(R.id.contents);
            toDoListModifyDate = itemView.findViewById(R.id.modifyDate);
            toDoListCheckDate = itemView.findViewById(R.id.checkDate);
            layout = itemView;
        }
    }

    public ToDoListAdapter(OrderedRealmCollection<ToDoList> data) {
        super(data, true);
        //setHasStableIds(true);
    }

    // onCreateViewHolder(): Item View를 위한 View Holder 객체를 생성하여 리턴
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.todolist_item, parent, false);
        ViewHolder vh  = new ViewHolder(view);

        return vh;
    }

    // onBindViewHolder(): position의 데이터를 ViewHolder의 Item View에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ToDoList mData = getItem(position);

        final long id = mData.getId();
        String title = mData.getTitle();
        String contents = mData.getContents();
        Date modifyDate = mData.getModifyDate();
        boolean checkDone = mData.getCheckDone();
        Date checkDate = mData.getCheckDate();

        holder.toDoListTitle.setText(title);
        holder.toDoListContents.setText(contents);
        holder.toDoListModifyDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(modifyDate));
        if (checkDone) {
            holder.toDoListCheckDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(checkDate));
            holder.toDoListTitle.setPaintFlags(holder.toDoListTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.toDoListContents.setPaintFlags(holder.toDoListContents.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.toDoListCheckDate.setText("");
            holder.toDoListTitle.setPaintFlags(0);
            holder.toDoListContents.setPaintFlags(0);
        }

        holder.layout.setOnClickListener((v)-> {
                Intent intent = new Intent(v.getContext(), AddToDoList.class);
                intent.putExtra("id", id);
                intent.putExtra("date", modifyDate);
                v.getContext().startActivity(intent);
        });

        holder.layout.setOnLongClickListener((v)-> {
            realm = Realm.getDefaultInstance();
            ToDoList toDoList = realm.where(ToDoList.class).equalTo("id", id).findFirst();
            realm.executeTransaction((realm)-> {
                if (!toDoList.getCheckDone()) {
                    toDoList.setCheckDate(new Date(System.currentTimeMillis()));
                    toDoList.setCheckDone(true);
                } else {
                    toDoList.setCheckDone(false);
                }
            });
            realm.beginTransaction();
            realm.commitTransaction();
            realm.close();
            // notify 방안 고민
            /*if (!toDoList.getCheckDone()) {
                notifyItemMoved(position, getItemCount()-1);
            } else {
                notifyDataSetChanged();
            }*/
            return true;
        });
    }

    // setHasStableIds(), getItemID(int index) 시 리스트가 화면 크기보다 커졌을 때 Item 위치 변경하면 update되지 않음
    // 해결방안 고민
    /*@Override
    public long getItemId(int index) {
        return getItem(index).getId();
    }*/

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return true;
    }

    @Override
    public void onItemSwipe(int position, int direction) {
        ToDoList mData = getItem(position);
        long id = mData.getId();

        realm = Realm.getDefaultInstance();
        ToDoList results = realm.where(ToDoList.class).equalTo("id", id).findFirst();
        realm.executeTransaction((realm)->results.deleteFromRealm());
        realm.beginTransaction();
        realm.commitTransaction();
        realm.close();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }
}
