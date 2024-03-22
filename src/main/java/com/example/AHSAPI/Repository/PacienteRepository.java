package com.example.AHSAPI.Repository;

import com.example.AHSAPI.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByNumerodocumento(int numerodocumento);

    @Query(value = "INSERT INTO pacscan (numerodocumento, apellido, nombre) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void guardarPaciente(int numeroDocumento, String apellido, String nombre);
}
