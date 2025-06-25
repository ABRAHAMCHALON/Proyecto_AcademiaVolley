package com.example.proyecto_academiavolley.ui.asistencia;

public class Grupo {
    private String id_grupo;
    private String nom_grupo;
    private String dias_horario;
    private String hinicio_horario;
    private String hfin_horario;

    // Constructor
    public Grupo(String id_grupo, String nom_grupo, String dias_horario, String hinicio_horario, String hfin_horario) {
        this.id_grupo = id_grupo;
        this.nom_grupo = nom_grupo;
        this.dias_horario = dias_horario;
        this.hinicio_horario = hinicio_horario;
        this.hfin_horario = hfin_horario;
    }

    // Métodos getter y setter (si es necesario)
    public String getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(String id_grupo) {
        this.id_grupo = id_grupo;
    }

    public String getNom_grupo() {
        return nom_grupo;
    }

    public void setNom_grupo(String nom_grupo) {
        this.nom_grupo = nom_grupo;
    }

    public String getDias_horario() {
        return dias_horario;
    }

    public void setDias_horario(String dias_horario) {
        this.dias_horario = dias_horario;
    }

    public String getHinicio_horario() {
        return hinicio_horario;
    }

    public void setHinicio_horario(String hinicio_horario) {
        this.hinicio_horario = hinicio_horario;
    }

    public String getHfin_horario() {
        return hfin_horario;
    }

    public void setHfin_horario(String hfin_horario) {
        this.hfin_horario = hfin_horario;
    }
}



