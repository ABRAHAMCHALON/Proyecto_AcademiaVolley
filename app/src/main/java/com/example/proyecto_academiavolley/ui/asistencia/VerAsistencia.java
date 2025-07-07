package com.example.proyecto_academiavolley.ui.asistencia;

public class VerAsistencia {
    String idAsistencia;
    String nombreAlumno;
    String apellidoAlumno;
    String tipoAsistencia;

    public VerAsistencia(String idAsistencia, String nombreAlumno, String apellidoAlumno, String tipoAsistencia) {
        this.idAsistencia = idAsistencia;
        this.nombreAlumno = nombreAlumno;
        this.apellidoAlumno = apellidoAlumno;
        this.tipoAsistencia = tipoAsistencia;
    }
    public String getIdAsistencia() {
        return idAsistencia;
    }
    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public String getApellidoAlumno() {
        return apellidoAlumno;
    }

    public String getTipoAsistencia() {
        return tipoAsistencia;
    }
}
