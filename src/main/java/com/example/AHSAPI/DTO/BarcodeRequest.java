package com.example.AHSAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BarcodeRequest {
    private String barcodeData;
    // este es el JSON
    public PacienteResponse barcodeToPaciente(){
        PacienteResponse pacienteResponse = new PacienteResponse()
        // algoritmo para transformar el string a un PacienteResponse
    }
}
