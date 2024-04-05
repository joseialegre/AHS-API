package com.example.AHSAPI.Repository;

import com.example.AHSAPI.DTO.PacienteDTO;
import com.example.AHSAPI.DTO.PacienteResponse;
import com.example.AHSAPI.Entity.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {


    Optional<Paciente> findByNumerodocumento(int numerodocumento);

    // Método para ejecutar una consulta SQL nativa para guardar un paciente
    @Query(value = "INSERT INTO pacscan (numerodocumento, apellido, nombre) VALUES (?1, ?2, ?3)", nativeQuery = true)
    Optional<PacienteResponse> save(int numeroDocumento, String apellido, String nombre);

//    @Procedure
//    List<Paciente> BuscarPorNumeroDocumento(int numerodocumento);
    @Procedure
    List<Paciente> BuscarPorNumeroDocumento(int numerodocumento);

    @Procedure
    void InstanciarPaciente(String apellido, String nombre, int numerodocumento, int numerotramite,
                            char ejemplar, Timestamp fechaemision, Timestamp fechavto,
                            int idtipodoc, char sexo, Timestamp fechanac);
}
