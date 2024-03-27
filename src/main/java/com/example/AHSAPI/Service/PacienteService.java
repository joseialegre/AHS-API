package com.example.AHSAPI.Service;

import com.example.AHSAPI.DTO.BarcodeRequest;
import com.example.AHSAPI.DTO.PacienteResponse;
import com.example.AHSAPI.Entity.Paciente;
import com.example.AHSAPI.Repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Paciente> getPacientesByNumeroDocumento(int numerodocumento){
        List<Paciente> pacientes = pacienteRepository.BuscarPorNumeroDocumento(numerodocumento);
        return pacientes;
    }



    public void savePaciente(Paciente paciente){
        pacienteRepository.save(paciente);
    }


    public PacienteResponse singlePaciente(PacienteResponse pacienteResponse){
        //que hago si el paciente no se repite
        return pacienteResponse;
    }

    public PacienteResponse multiplePaciente(PacienteResponse pacienteResponse){
        //que hago si el paciente se repite
        return pacienteResponse;
    }

    public PacienteResponse nonePaciente(PacienteResponse pacienteResponse){
        //que hago no esta el paciente
        return pacienteResponse;
    }

    public PacienteResponse getPacienteResponse(BarcodeRequest barcodeRequest){


        PacienteResponse pacienteResponse = barcodeRequest.barcodeToPaciente();

        if(pacienteResponse.getNumerodocumento() == 0){
            pacienteResponse.setRegistrado("Datos invalidos");
            return pacienteResponse;
        }

        try{
            //busco en la base de datos, si existe devuelvo los datos.


//            Paciente paciente = getPacienteByNumeroDocumento(pacienteResponse.getNumerodocumento());
            List<Paciente> pacientes = getPacientesByNumeroDocumento(pacienteResponse.getNumerodocumento());

            if(pacientes.size() == 1){

            }
            else {

            }

            switch (pacientes.size()){
                case 1: singlePaciente(pacienteResponse); break;
                case 0: nonePaciente(pacienteResponse); break;
                default: multiplePaciente(pacienteResponse); break;
            }

            //Aqui si la lista tiene mas de un elemento significa que el paciente esta dos veces.
            //si la lista tiene mas de un elemento tengo que ir a la funcion
            //donde compara y modifica los datos.

            //si la lista tiene solo un elemento hago lo de siempre


            pacienteResponse.setRegistrado("El paciente ya esta registrado en la BD");
        }
        catch(Exception e){
            try{
                Paciente paciente = new Paciente(pacienteResponse.getNumerodocumento(),pacienteResponse.getApellido(),pacienteResponse.getNombre());
                savePaciente(paciente);
                //pacienteRepository.save(pacienteResponse.getNumerodocumento(),pacienteResponse.getApellido(),pacienteResponse.getNombre());
                pacienteResponse.setRegistrado("El paciente no estaba en la BD, se lo ha registrado");
            }
            catch (Exception e2) {
                pacienteResponse.setRegistrado(e2.toString());
            }
        }
        return pacienteResponse;
    }
}
