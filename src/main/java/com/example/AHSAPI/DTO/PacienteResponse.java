package com.example.AHSAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PacienteResponse {
    private String apellido;
    private String nombre;
    private int numerodocumento;
    private String registrado;
}
