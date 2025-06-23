package com.example.proyecto_academiavolley.ui.alumno;

public class Alumno {

    String id;
    String nombres;
    String apellidos;
    String ndc;
    String edad;
    String fnacimiento;
    String grupo;
    String horario;


    String estado;

    String nombresApoderado;
    String apellidosApoderado;
    String cel;
    String dir;


    public Alumno(String id, String nombres, String apellidos, String ndc, String edad, String fnacimiento, String grupo, String horario, String nombresApoderado, String apellidosApoderado, String cel, String dir) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.ndc = ndc;
        this.edad = edad;
        this.fnacimiento = fnacimiento;
        this.grupo = grupo;
        this.horario = horario;
        this.nombresApoderado = nombresApoderado;
        this.apellidosApoderado = apellidosApoderado;
        this.cel = cel;
        this.dir = dir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getFnacimiento() {
        return fnacimiento;
    }

    public void setFnacimiento(String fnacimiento) {
        this.fnacimiento = fnacimiento;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getNombresApoderado() {
        return nombresApoderado;
    }

    public void setNombresApoderado(String nombresApoderado) {
        this.nombresApoderado = nombresApoderado;
    }

    public String getApellidosApoderado() {
        return apellidosApoderado;
    }

    public void setApellidosApoderado(String apellidosApoderado) {
        this.apellidosApoderado = apellidosApoderado;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getCel() {
        return cel;
    }

    public void setCel(String cel) {
        this.cel = cel;
    }
}
