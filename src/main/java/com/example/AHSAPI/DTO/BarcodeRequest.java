package com.example.AHSAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BarcodeRequest {
    private String barcodeData;
    // este es el JSON
    public PacienteResponse barcodeToPaciente() {
        PacienteResponse pacienteResponse = new PacienteResponse();
        String[] parts;

        if (barcodeData.startsWith("0")) { // Formato 1
            parts = barcodeData.split("@|\"");
            if (parts.length >= 8) {
                try {
                    pacienteResponse.setNumerodocumento(Integer.parseInt(parts[4]));
                    pacienteResponse.setApellido(parts[1]);
                    pacienteResponse.setNombre(parts[2]);
                    pacienteResponse.setNumerotramite(Integer.parseInt(parts[0]));
                    pacienteResponse.setSexo(parts[3].charAt(0));
                    pacienteResponse.setFechanac(stringToDate(parts[6]));
                    pacienteResponse.setIdtipodoc(3);
                    pacienteResponse.setFechaemision(stringToDate(parts[7]));
                    pacienteResponse.setEjemplar(parts[5].charAt(0));

                } catch (Exception e) {
                    pacienteResponse.setNumerodocumento(0);
                    pacienteResponse.setRegistrado(e.toString());
                }
            }
        } else if (barcodeData.startsWith("@")) { // Formato 2
            parts = barcodeData.split("@|\"");
            if (parts.length >= 6) {
                try {
                    pacienteResponse.setNumerodocumento(Integer.parseInt(parts[1]));
                    pacienteResponse.setApellido(parts[4]);
                    pacienteResponse.setNombre(parts[5]);
                    pacienteResponse.setEjemplar(parts[2].charAt(0));
                    pacienteResponse.setFechanac(stringToDate(parts[7]));
                    pacienteResponse.setSexo(parts[8].charAt(0));
                    pacienteResponse.setFechaemision(stringToDate(parts[9]));
                    pacienteResponse.setNumerotramite(Integer.parseInt(parts[10]));
                    pacienteResponse.setFechavto(stringToDate(parts[12]));
                    pacienteResponse.setIdtipodoc(3);
                } catch (Exception e) {
                    pacienteResponse.setError(e.toString());
                }
            }
        }
        return pacienteResponse;
    }

    private Timestamp stringToDate(String string){
        String dateString = string;

        // Parseamos el string a un objeto LocalDate
        LocalDate localDate = LocalDate.parse(dateString, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Creamos un Timestamp a partir del LocalDate
        Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());

        return timestamp;
    }

}
