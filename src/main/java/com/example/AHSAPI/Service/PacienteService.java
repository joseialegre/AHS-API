package com.example.AHSAPI.Service;

import com.example.AHSAPI.DTO.BarcodeRequest;
import com.example.AHSAPI.DTO.PacienteDTO;
import com.example.AHSAPI.DTO.PacienteResponse;
import com.example.AHSAPI.Entity.Paciente;
import com.example.AHSAPI.Repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
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
//    @Transactional
    public List<Paciente> getPacientesByNumeroDocumento(int numerodocumento){
        List<Paciente> pacientes = pacienteRepository.BuscarPorNumeroDocumento(numerodocumento);
        return pacientes;
    }

    public void setPaciente(String apellido, String nombre, int numerodocumento, int numerotramite,
                            char ejemplar, Timestamp fechaemision, Timestamp fechavto,
                            int idtipodoc, char sexo, Timestamp fechanac){
        pacienteRepository.InstanciarPaciente(apellido, nombre, numerodocumento, numerotramite,
                ejemplar, fechaemision, fechavto, idtipodoc, sexo, fechanac);
    }



//    public void savePaciente(Paciente paciente){
//        pacienteRepository.save(paciente);
//    }


    public PacienteResponse singlePaciente(List<Paciente> pacientes, PacienteResponse pacienteResponse){
        //que hago si el paciente no se repite

        pacienteDataVerify(pacientes,pacienteResponse);
        pacienteResponse.setRegistrado("EL PACIENTE YA ESTA REGISTRADO");
        return pacienteResponse;
    }

    public PacienteResponse multiplePaciente(List<Paciente> pacientes, PacienteResponse pacienteResponse){
        //que hago si el paciente se repite
        return pacienteResponse;
    }

    public PacienteResponse nonePaciente(PacienteResponse pacienteResponse){
        //que hago si no esta el paciente
        //instanciar al paciente en la BD
        //Paciente paciente = new Paciente(pacienteResponse.getNumerodocumento(),pacienteResponse.getApellido(),pacienteResponse.getNombre());
        //savePaciente(paciente);

        setPaciente(pacienteResponse.getApellido(), pacienteResponse.getNombre(), pacienteResponse.getNumerodocumento(),
                pacienteResponse.getNumerotramite(), pacienteResponse.getEjemplar(),pacienteResponse.getFechaemision(),pacienteResponse.getFechavto(),
                pacienteResponse.getIdtipodoc(),pacienteResponse.getSexo(),pacienteResponse.getFechanac());

        pacienteResponse.setRegistrado("EL PACIENTE FUE REGISTRADO");
        return pacienteResponse;
    }
    @Transactional
    public PacienteResponse getPacienteResponse(BarcodeRequest barcodeRequest){


        PacienteResponse pacienteResponse = barcodeRequest.barcodeToPaciente();

        if(pacienteResponse.getNumerodocumento() == 0){
            //Error 404 input invalido
            //pacienteResponse.setRegistrado("Datos invalidos");
            return pacienteResponse;
        }

        //busco en la base de datos, si existe devuelvo los datos.
        //traigo los pacientes de la BD
        try {
            List<Paciente> pacientes = getPacientesByNumeroDocumento(pacienteResponse.getNumerodocumento());

            switch (pacientes.size()) {
                case 1:
                    singlePaciente(pacientes, pacienteResponse);
                    break;
                case 0:
                    nonePaciente(pacienteResponse);
                    break;
                default:
                    multiplePaciente(pacientes, pacienteResponse);
                    break;
            }

        }catch (Exception e){
            pacienteResponse.setRegistrado(e.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return pacienteResponse;
        }
        //Aqui si la lista tiene mas de un elemento significa que el paciente esta dos veces.
        //si la lista tiene mas de un elemento tengo que ir a la funcion
        //donde compara y modifica los datos.

        //si la lista tiene solo un elemento hago lo de siempre
        return pacienteResponse;
    }

    public PacienteResponse pacienteDataVerify(List<Paciente> pacientes, PacienteResponse pacienteResponse){
        //comparo el numero de tramite.
        if(pacientes.get(0).getNumerotramite()!=pacienteResponse.getNumerotramite()){//si numero de tramite es diferente

            int comparacion = pacientes.get(0).getFechaemision().compareTo(pacienteResponse.getFechaemision());
            //si mi fecha emision guardada es menor a la nueva
            if(comparacion < 0) {
                return nonePaciente(pacienteResponse);
            }

        }
        //otro campo es diferente
            //actualizo
        return pacienteResponse;
    }
}
//tengo la lista. busco si se repite algun numerotramite, si re repite. busco si mi fechaemision es mayor a todas.