package com.example.monitorapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntryDao {
    @Query("SELECT * FROM entry")
    List<Entry> getAll();

    @Query("SELECT * FROM entry WHERE synced <> 1")
    List<Entry> getAllNotSynced();

    @Query("SELECT * FROM entry WHERE uid IN (:entryIds)")
    List<Entry> loadAllByIds(int[] entryIds);

    @Query("SELECT * FROM entry ORDER BY uid DESC LIMIT (:rowsCount) OFFSET (:offset)")
    List<Entry> getRecent(int rowsCount, int offset);

    @Query("SELECT * FROM entry ORDER BY uid DESC LIMIT 1")
    Entry getLast();

    @Query("SELECT * FROM entry WHERE uid = (:uid) LIMIT 1")
    Entry getById(int uid);

    @Query("SELECT * FROM entry WHERE full_name LIKE :full_name LIMIT 1")
    Entry findByName(String full_name);

    @Insert
    void insert(Entry entry);

    @Update
    void update(Entry entry);

    @Insert
    void insertAll(Entry... entries);

    @Delete
    void delete(Entry entry);

    @Query("DELETE FROM entry")
    void clearAllEntries();
}
