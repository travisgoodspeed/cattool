package com.kk4vcz.goodspeedscattool.ui.codeplug;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CodeplugViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CodeplugViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the codeplug fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}