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

    private String apellido;
    private String nombre;
    private int numerodocumento;
    private int numerotramite;
    private char ejemplar;
    private Timestamp fechaemision;
    private Timestamp fechavto;
    private int idtipodoc;
    private char sexo;
    private Timestamp fechanac;

    private String registrado;
}
