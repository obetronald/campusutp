package com.example.guiabeaconmaster2.Entitys;

import java.sql.Date;

public class Horario {
    private int id_horario;
    private String cod_beacon;
    private String nombre_beacon;
    private String hora_inicio;
    private String hora_fin;
    private String nombre_curso;
    private String nombre_docente;
    private String imagen;

    public Horario(){}

    public Horario(int id_horario, String cod_beacon, String nombre_beacon, String hora_inicio, String hora_fin, String nombre_curso, String nombre_docente, String imagen) {
        this.id_horario = id_horario;
        this.cod_beacon = cod_beacon;
        this.nombre_beacon = nombre_beacon;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.nombre_curso = nombre_curso;
        this.nombre_docente = nombre_docente;
        this.imagen = imagen;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }



    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getNombre_docente() {
        return nombre_docente;
    }

    public void setNombre_docente(String nombre_docente) {
        this.nombre_docente = nombre_docente;
    }

    public String getNombre_curso() {
        return nombre_curso;
    }

    public void setNombre_curso(String nombre_curso) {
        this.nombre_curso = nombre_curso;
    }

    public String getNombre_beacon() {
        return nombre_beacon;
    }

    public void setNombre_beacon(String nombre_beacon) {
        this.nombre_beacon = nombre_beacon;
    }

    public int getId_horario() {
        return id_horario;
    }

    public void setId_horario(int id_horario) {
        this.id_horario = id_horario;
    }

    public String getCod_beacon() {
        return cod_beacon;
    }

    public void setCod_beacon(String cod_beacon) {
        this.cod_beacon = cod_beacon;
    }

    @Override
    public String toString() {
        return  "|"+hora_inicio+"|"+hora_fin+"|"+ nombre_curso+ "|"+ nombre_docente+"|";
    }
}