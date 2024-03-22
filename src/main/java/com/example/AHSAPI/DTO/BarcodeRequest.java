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
    public PacienteResponse barcodeToPaciente() {
        PacienteResponse pacienteResponse = new PacienteResponse();
        if (barcodeData.startsWith("0")) { // Formato 1
            String[] parts = barcodeData.split("@");
            if (parts.length >= 8) {
                pacienteResponse.setNumerodocumento(Integer.parseInt(parts[4]));
                pacienteResponse.setApellido(parts[1]);
                pacienteResponse.setNombre(parts[2]);
            }
        } else if (barcodeData.startsWith("@")) { // Formato 2
            String[] parts = barcodeData.split("@");
            if (parts.length >= 6) {
                pacienteResponse.setNumerodocumento(Integer.parseInt(parts[1]));
                pacienteResponse.setApellido(parts[4]);
                pacienteResponse.setNombre(parts[5]);
            }
        }
        return pacienteResponse;
    }
}
