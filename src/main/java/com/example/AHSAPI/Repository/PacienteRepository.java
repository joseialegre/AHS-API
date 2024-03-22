package com.example.AHSAPI.Repository;

import com.example.AHSAPI.DTO.PacienteResponse;
import com.example.AHSAPI.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByNumerodocumento(int numerodocumento);

    // Método para ejecutar una consulta SQL nativa para guardar un paciente
    @Query(value = "INSERT INTO pacscan (numerodocumento, apellido, nombre) VALUES (?1, ?2, ?3)", nativeQuery = true)
    Optional<PacienteResponse> save(int numeroDocumento, String apellido, String nombre);
}
