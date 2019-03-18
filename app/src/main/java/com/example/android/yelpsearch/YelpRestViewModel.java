package com.example.android.yelpsearch;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.android.yelpsearch.data.YelpRest;
import com.example.android.yelpsearch.data.YelpRestRepository;

import java.util.List;

public class YelpRestViewModel extends AndroidViewModel {
    private YelpRestRepository mYelpRestRepository;

    public YelpRestViewModel(Application application) {
        super(application);
        mYelpRestRepository = new YelpRestRepository(application);
    }

    public void insertYelpRest(YelpRest restaurant) {
        mYelpRestRepository.insertYelpRest(restaurant);
    }

    public void deleteYelpRest(YelpRest restaurant) {
        mYelpRestRepository.deleteYelpRest(restaurant);
    }

    public LiveData<List<YelpRest>> getAllYelpRests() {
        return mYelpRestRepository.getAllYelpRests();
    }

    public LiveData<YelpRest> getYelpRestByName(String fullName) {
        return mYelpRestRepository.getYelpRestByName(fullName);
    }
}
