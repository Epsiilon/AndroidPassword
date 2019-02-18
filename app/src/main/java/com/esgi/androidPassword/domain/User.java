package com.esgi.androidPassword.dao;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
@Entity(tableName = "USER")
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @Nullable
    private String lastName;

    @Nullable
    private String firstName;

    private String passwordKey;

    public User(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    @Nullable
    public String getFirstName() {
        return firstName;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

}