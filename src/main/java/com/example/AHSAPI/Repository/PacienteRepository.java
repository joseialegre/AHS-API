package com.example.AHSAPI.Repository;

import com.example.AHSAPI.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    // Aquí puedes agregar métodos adicionales para consultar la tabla de clientes si es necesario
    Optional<Paciente> findByNumerodocumento(int numerodocumento);



}
