package com.example.proyecto_academiavolley.ui.grupo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GrupoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GrupoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}