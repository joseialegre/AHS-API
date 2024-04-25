package com.example.AHSAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pacscan")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpaciente")
    private int idpaciente;
    private String apellido;
    private String nombre;
    private int idtipodoc; //3
    private int numerodocumento;
    private Timestamp fechanac;
    private char sexo;
    private int numerotramite;
    private char ejemplar;
    private Timestamp fechaemision;
    private Timestamp fechavto;



}