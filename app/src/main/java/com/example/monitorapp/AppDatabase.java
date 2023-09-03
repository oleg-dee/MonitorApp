package com.example.monitorapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Entry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EntryDao entryDao();
}

