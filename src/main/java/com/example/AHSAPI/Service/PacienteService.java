package com.example.AHSAPI.Service;

import com.example.AHSAPI.Entity.Paciente;
import com.example.AHSAPI.Repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    // datos que mando
    // DNI, Apellido, Nombre, Direccion, Fecha Nacimiento, Sexo

    public Paciente getPacienteByNumeroDocumento(int numerodocumento){
        return pacienteRepository.findByNumerodocumento(numerodocumento);
    }

}
