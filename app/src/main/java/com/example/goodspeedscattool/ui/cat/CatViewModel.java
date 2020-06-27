package com.example.goodspeedscattool.ui.cat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CatViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the CAT fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}