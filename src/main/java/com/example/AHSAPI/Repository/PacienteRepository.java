package com.example.AHSAPI.Repository;



import com.example.AHSAPI.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;


import java.sql.Timestamp;
import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    @Procedure
    List<Paciente> BuscarPorNumeroDocumento(int numerodocumento);

    @Procedure
    void InstanciarPaciente(String apellido, String nombre, int numerodocumento, int numerotramite,
                            char ejemplar, Timestamp fechaemision, Timestamp fechavto,
                            int idtipodoc, char sexo, Timestamp fechanac);
}
