package com.example.todolist;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ToDoListApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("todolist.realm").build();
        Realm.setDefaultConfiguration(config);
    }
}
