package com.example.AHSAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pacscan")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpaciente")
    private int idpaciente;
    private int numhistclinica;
    private String apellido;
    private String nombre;
    private int idtipodoc; //3
    private int numerodocumento;
    private String direccion;
    private int idlocalidad;
    private Timestamp fechanac;
    private char sexo;
    private String telefono;
    private int idobrasocial;
    private String nroafiliado;
    private String nombrepadre;
    private String nombremadre;
    private int idusuario;
    private Timestamp fechaalta;
    private Timestamp fechamodif;
    private char subidomsp;
    private String mail;
    private int idpacinter3;
    private String origen;
    private char estado;
    private boolean federado;
    private String fechadef;
    private String notadelpac;
    private int idprointer3modif;
    private int idestreferencia;
    private String domicilioreal;
    private int numerotramite;
    private int ejemplar;
    private Timestamp fechaemision;
    private Timestamp fechavto;

    public Paciente(int numerodocumento, String apellido, String nombre){
        setNumerodocumento(numerodocumento);
        setApellido(apellido);
        setNombre(nombre);
    }

    public Paciente(int numerotramite,int numerodocumento, char ejemplar, String apellido, String nombre, Timestamp fechaemision, Timestamp fechavto, char sexo, Timestamp fechanac ){
        setNumerotramite(numerotramite);
        setNumerodocumento(numerodocumento);
        setEjemplar(ejemplar);
        setApellido(apellido);
        setNombre(nombre);
        setFechaemision(fechaemision);
        setFechavto(fechavto);
        setIdtipodoc(3);
        setSexo(sexo);
        setFechanac(fechanac);
    }

}