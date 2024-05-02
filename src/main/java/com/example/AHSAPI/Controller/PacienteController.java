package com.example.AHSAPI.Controller;

import com.example.AHSAPI.DTO.BarcodeRequest;
import com.example.AHSAPI.DTO.PacienteResponse;
import com.example.AHSAPI.Service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class PacienteController {

    @Autowired
    private PacienteService service;

    @PostMapping()
    public PacienteResponse barcodeScanRequest(@RequestBody BarcodeRequest barcodeRequest){

        return service.getPacienteResponse(barcodeRequest);
    }

}
