package com.example.proyecto_academiavolley.ui.asistencia;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AsistenciaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AsistenciaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}