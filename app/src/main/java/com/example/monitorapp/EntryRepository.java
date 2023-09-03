package com.example.monitorapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EntryRepository {

    private static final String DB_NAME = "AppDB";
    private AppDatabase db;

    public EntryRepository(Context context) {
        RoomDatabase.Builder<AppDatabase> dbBuilder = Room.databaseBuilder(context, AppDatabase.class, DB_NAME);
        dbBuilder.setQueryCallback((sqlQuery, bindArgs) -> System.out.println("SQL Query: "+sqlQuery+" SQL Args: "+bindArgs),Executors.newSingleThreadExecutor());
        db = dbBuilder.build();

    }

    public void insert(final Entry entry) {
        Callable<Entry> callable = new Callable<Entry>() {
            @Override
            public Entry call() throws Exception {
                db.entryDao().insert(entry);
                return null;
            }
        };
        Future future = Executors.newSingleThreadExecutor().submit(callable);
    }

    public void insertAll(final List<Entry> entryList) {
        Callable<Entry> callable = new Callable<Entry>() {
            @Override
            public Entry call() throws Exception {
                db.entryDao().insertAll((Entry[]) entryList.toArray());
                return null;
            }
        };
        Future future = Executors.newSingleThreadExecutor().submit(callable);
    }

    public void update(final Entry entry) {
        Callable<Entry> callable = new Callable<Entry>() {
            @Override
            public Entry call() throws Exception {
                db.entryDao().update(entry);
                return null;
            }
        };
        Future future = Executors.newSingleThreadExecutor().submit(callable);
    }

    public void delete(final Entry entry) {
        Callable<Entry> callable = new Callable<Entry>() {
            @Override
            public Entry call() throws Exception {
                db.entryDao().delete(entry);
                return null;
            }
        };
        Future future = Executors.newSingleThreadExecutor().submit(callable);
    }

    public void clearAllEntries() {
        Callable<Entry> callable = new Callable<Entry>() {
            @Override
            public Entry call() throws Exception {
                db.entryDao().clearAllEntries();
                return null;
            }
        };
        Future future = Executors.newSingleThreadExecutor().submit(callable);
    }


    public List<Entry> getAll() throws ExecutionException, InterruptedException {
        Callable<List<Entry>> callable = new Callable<List<Entry>>() {
            @Override
            public List<Entry> call() throws Exception {
                return db.entryDao().getAll();
            }
        };
        Future<List<Entry>> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Entry> getAllNotSynced() throws ExecutionException, InterruptedException {
        Callable<List<Entry>> callable = new Callable<List<Entry>>() {
            @Override
            public List<Entry> call() throws Exception {
                return db.entryDao().getAllNotSynced();
            }
        };
        Future<List<Entry>> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Entry getLast() throws ExecutionException, InterruptedException {
        Callable<Entry> callable = new Callable<Entry>() {
            @Override
            public Entry call() throws Exception {
                return db.entryDao().getLast();
            }
        };
        Future<Entry> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Entry getById(int uid) throws ExecutionException, InterruptedException {
        Callable<Entry> callable = new Callable<Entry>() {
            @Override
            public Entry call() throws Exception {
                return db.entryDao().getById(uid);
            }
        };
        Future<Entry> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }


    public List<Entry> getRecent(int rowsCount, int offset) throws ExecutionException, InterruptedException {
        Callable<List<Entry>> callable = new Callable<List<Entry>>() {
            @Override
            public List<Entry> call() throws Exception {
                return db.entryDao().getRecent(rowsCount, offset);
            }
        };
        Future<List<Entry>> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

}
