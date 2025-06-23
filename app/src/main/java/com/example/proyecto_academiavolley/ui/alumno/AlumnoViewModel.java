package com.example.proyecto_academiavolley.ui.alumno;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlumnoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AlumnoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}