package com.example.AHSAPI.Controller;

import com.example.AHSAPI.Entity.Paciente;
import com.example.AHSAPI.Service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PacienteController {

    @Autowired
    private PacienteService service;

    @GetMapping ("/pacienteByNumeroDocumento/{numerodocumento}")
    public Paciente findByNumeroDocumento(@PathVariable int numerodocumento){
        return service.getPacienteByNumeroDocumento(numerodocumento);
    }
}
