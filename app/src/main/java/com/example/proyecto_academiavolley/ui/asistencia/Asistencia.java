package com.example.proyecto_academiavolley.ui.asistencia;

import android.widget.RadioButton;

public class Asistencia {
    String id_alumno;
    String nombre;
    String apellido;
    RadioButton estAsistio;
    RadioButton estFalto;
    String tipoAsistencia;

    // Constructor
    public Asistencia(String id_alumno, String nombre, String apellido, RadioButton estAsistio, RadioButton estFalto) {
        this.id_alumno = id_alumno;
        this.nombre = nombre;
        this.apellido = apellido;
        this.estAsistio = estAsistio;
        this.estFalto = estFalto;
        this.tipoAsistencia = ""; // Inicialmente no se ha seleccionado nada
    }

    // Getters y Setters
    public String getId_alumno() {
        return id_alumno;
    }

    public void setId_alumno(String id_alumno) {
        this.id_alumno = id_alumno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public RadioButton getEstAsistio() {
        return estAsistio;
    }

    public void setEstAsistio(RadioButton estAsistio) {
        this.estAsistio = estAsistio;
    }

    public RadioButton getEstFalto() {
        return estFalto;
    }

    public void setEstFalto(RadioButton estFalto) {
        this.estFalto = estFalto;
    }

    public String getTipoAsistencia() {
        return tipoAsistencia;
    }

    public void setTipoAsistencia(String tipoAsistencia) {
        this.tipoAsistencia = tipoAsistencia;
    }
}
