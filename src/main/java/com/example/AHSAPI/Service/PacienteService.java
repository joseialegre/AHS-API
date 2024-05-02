package com.example.AHSAPI.Service;

import com.example.AHSAPI.DTO.BarcodeRequest;
import com.example.AHSAPI.DTO.PacienteResponse;
import com.example.AHSAPI.Entity.Paciente;
import com.example.AHSAPI.Repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;



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

    public PacienteResponse instanciarPaciente(PacienteResponse pacienteResponse){
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
            pacienteResponse.setRegistrado("Datos invalidos");
            return pacienteResponse;
        }
        try {
            List<Paciente> pacientes = getPacientesByNumeroDocumento(pacienteResponse.getNumerodocumento());
            if(pacientes.size()!=0){
                pacienteTreatment(pacientes,pacienteResponse);
            }else{
                instanciarPaciente(pacienteResponse);
            }
        }catch (Exception e){
            pacienteResponse.setRegistrado(e.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return pacienteResponse;
        }
        return pacienteResponse;
    }
    public PacienteResponse pacienteTreatment(List<Paciente> pacientes, PacienteResponse pacienteResponse){
        int indexTemp=-1;
        for(int i=0;i<pacientes.size();i++){
            if(pacientes.get(i).getNumerotramite()==pacienteResponse.getNumerotramite()){
                //es el mismo numerotramite. no hago nada. muestro los datos guardados
                pacienteResponse.setRegistrado("PACIENTE ESTA ACTUALIZADO");
                return pacienteResponse;
            }else{
                if(indexTemp==-1){
                    indexTemp=i;
                }
                else{
                    if(TimestampComparison(pacientes.get(i).getFechaemision(),pacientes.get(indexTemp).getFechaemision())){
                        indexTemp=i;
                    }
                }
            }
        }
        if(indexTemp!=-1){
            if(TimestampComparison(pacienteResponse.getFechaemision(),pacientes.get(indexTemp).getFechaemision())){
                //insertar paciente
                return instanciarPaciente(pacienteResponse);
            }else{

                return pacienteMapping(pacientes,pacienteResponse,indexTemp);
            }
        }

        return pacienteResponse;
    }

    public boolean TimestampComparison(Timestamp a,Timestamp b){
        int x = a.compareTo(b);
        if(x>0) return true;
        else return false;
    }

    public PacienteResponse pacienteMapping(List<Paciente> pacientes, PacienteResponse pacienteResponse, int indexTemp){
        pacienteResponse.setNombre(pacientes.get(indexTemp).getNombre());
        pacienteResponse.setApellido(pacientes.get(indexTemp).getApellido());
        pacienteResponse.setNumerodocumento(pacientes.get(indexTemp).getNumerodocumento());
        pacienteResponse.setNumerotramite(pacientes.get(indexTemp).getNumerotramite());
        pacienteResponse.setEjemplar(pacientes.get(indexTemp).getEjemplar());
        pacienteResponse.setFechaemision(pacientes.get(indexTemp).getFechaemision());
        pacienteResponse.setFechavto(pacientes.get(indexTemp).getFechavto());
        pacienteResponse.setIdtipodoc(pacientes.get(indexTemp).getIdtipodoc());
        pacienteResponse.setSexo(pacientes.get(indexTemp).getSexo());
        pacienteResponse.setFechanac(pacientes.get(indexTemp).getFechanac());
        pacienteResponse.setRegistrado("DATOS MAS RECIENTES");
        return pacienteResponse;
    }
}
