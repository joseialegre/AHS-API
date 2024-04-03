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

    public void setPaciente(
            int numerodocumento,
            int numerotramite,
            String apellido,
            String nombre,
            char sexo,
            Timestamp fechaemision,
            Timestamp fechavto,
            Timestamp fechanac,
            char ejemplar,
            int idtipodoc


        ){
        pacienteRepository.InstanciarPaciente(
                numerodocumento,
                numerotramite,
                apellido,
                nombre,
                sexo,
                fechaemision,
                fechavto,
                fechanac,
                ejemplar,
                idtipodoc
        );
    }



//    public void savePaciente(Paciente paciente){
//        pacienteRepository.save(paciente);
//    }


    public PacienteResponse singlePaciente(List<Paciente> pacientes, PacienteResponse pacienteResponse){
        //que hago si el paciente no se repite
        pacienteResponse.setNombre(pacientes.get(0).getNombre());
        pacienteResponse.setApellido(pacientes.get(0).getApellido());
        pacienteResponse.setRegistrado("EL PACIENTE YA ESTA REGISTRADO");
        //return pacienteDataVerify(pacientes, pacienteResponse);
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

        setPaciente(
                pacienteResponse.getNumerodocumento(),
                pacienteResponse.getNumerotramite(),
                pacienteResponse.getApellido(),
                pacienteResponse.getNombre(),
                pacienteResponse.getSexo(),
                pacienteResponse.getFechaemision(),
                pacienteResponse.getFechavto(),
                pacienteResponse.getFechanac(),
                pacienteResponse.getEjemplar(),
                pacienteResponse.getIdtipodoc());

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
            pacienteResponse.setRegistrado(e.toString()+"error en service");
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
        //if(pacientes.getFirst().numero)//si numero de tramite es diferente
            //si fecha emision es menor a la que esta en la BD, nada
            //si fecha emision es mayor a la que esta en la BD, inserto
        //otro campo es diferente
            //actualizo
        return pacienteResponse;
    }
}
