package com.example.AHSAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PacienteResponse {

    private int numerodocumento;
    private int numerotramite;
    private String apellido;
    private String nombre;
    private char sexo;
    private Timestamp fechaemision;
    private Timestamp fechavto;
    private Timestamp fechanac;
    private char ejemplar;
    private int idtipodoc;



    private String registrado;
    private int error;
}
