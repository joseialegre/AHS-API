package com.example.AHSAPI.Repository;

import com.example.AHSAPI.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    // Aquí puedes agregar métodos adicionales para consultar la tabla de clientes si es necesario
    Paciente findByNumerodocumento(int numerodocumento);


}
