package com.example.AHSAPI.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PacScan")
public class Paciente {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private boolean archived;
    private List<String> category;

}