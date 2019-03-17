package com.example.android.yelpsearch.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface YelpRestDao {
    @Insert
    void insert(YelpRest repo);

    @Delete
    void delete(YelpRest repo);

    @Query("SELECT * FROM repos")
    LiveData<List<YelpRest>> getAllRepos();

    @Query("SELECT * FROM repos WHERE full_name = :fullName LIMIT 1")
    LiveData<YelpRest> getRepoByName(String fullName);
}
