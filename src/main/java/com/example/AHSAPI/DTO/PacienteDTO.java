package com.example.AHSAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PacienteDTO {
    //lo que la BD me devuelve
    private int numerodocumento;
    private String apellido;
    private String nombre;
}
