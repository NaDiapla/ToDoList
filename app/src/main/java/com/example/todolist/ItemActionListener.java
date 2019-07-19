package com.example.todolist;

public interface ItemActionListener {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemSwipe(int position, int direction);
}
