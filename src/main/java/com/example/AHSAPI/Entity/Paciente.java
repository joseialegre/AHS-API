package com.example.AHSAPI.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pacscan")
public class Paciente {

    @Id
    @GeneratedValue
    private int idpaciente;
    private int numhistclinica;
    private String apellido;
    private String nombre;
    private int idtipodoc;
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
}