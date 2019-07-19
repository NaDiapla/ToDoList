package com.example.todolist;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ToDoList extends RealmObject {
    @PrimaryKey
    private long id;
    private String title;
    private String contents;
    private Date modifyDate;
    private boolean checkDone;
    private Date checkDate;

    public ToDoList() {
        checkDone = true;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public boolean getCheckDone() {
        return checkDone;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public void setCheckDone(boolean checkDone) {
        this.checkDone = checkDone;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }
}
