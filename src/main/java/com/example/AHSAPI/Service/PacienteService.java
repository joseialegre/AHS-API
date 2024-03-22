package com.example.AHSAPI.Service;

import com.example.AHSAPI.DTO.BarcodeRequest;
import com.example.AHSAPI.DTO.PacienteResponse;
import com.example.AHSAPI.Entity.Paciente;
import com.example.AHSAPI.Repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    // datos que mando
    // DNI, Apellido, Nombre, Direccion, Fecha Nacimiento, Sexo

    public Paciente getPacienteByNumeroDocumento(int numerodocumento){
        Optional<Paciente> paciente = pacienteRepository.findByNumerodocumento(numerodocumento);
        return paciente.get();
    }

    public void savePaciente(Paciente paciente){
        pacienteRepository.save(paciente);

    }

    public PacienteResponse getPacienteResponse(BarcodeRequest barcodeRequest){

        // tratar el barcode string
        PacienteResponse pacienteResponse = barcodeRequest.barcodeToPaciente();

        //Paciente paciente = new Paciente(pacienteResponse.getNumerodocumento(),pacienteResponse.getApellido(),pacienteResponse.getNombre());


        try{
            //busco en la base de datos, si existe devuelvo los datos.
            //aca tengo que comprobar si los datos coinciden

            Paciente paciente = getPacienteByNumeroDocumento(pacienteResponse.getNumerodocumento());
            pacienteResponse.setRegistrado("El paciente ya esta registrado en la BD");
        }
        catch(Exception e){
            try{

                //aca tengo que cargar el paciente
                Paciente paciente = new Paciente(pacienteResponse.getNumerodocumento(),pacienteResponse.getApellido(),pacienteResponse.getNombre());
                savePaciente(paciente);
                //pacienteRepository.save(pacienteResponse.getNumerodocumento(),pacienteResponse.getApellido(),pacienteResponse.getNombre());
                pacienteResponse.setRegistrado("El paciente no estaba en la BD, se lo ha registrado");
            }
            catch (Exception e2) {
                pacienteResponse.setRegistrado(e2.toString());
            }

        }


        //si existe DNI, retorno el objeto pacienteResponse (mapear)
        //si no existe, crear y  retornar pacienteResponse
        //agregar comprobacion si algun dato no coincide. contemplar?

        return pacienteResponse;
    }
}
