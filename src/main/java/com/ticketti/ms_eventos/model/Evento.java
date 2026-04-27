package com.ticketti.ms_eventos.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "Evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable= false, length= 50)
    private String nombre;

    @Column(nullable= false, length= 200)
    private String descripcion;

    @Column(nullable= false)
    private Date fecha;

    @Column(nullable= false, length= 50)
    private String categoria; //factory method

    @Column(nullable= false, length= 50)
    private String recinto; //temporal?

    @Column(nullable= false)
    private Integer aforo; //temporal

    @Column(nullable= false)
    private Integer stockAforo; //temporal

    @Column(nullable= false)
    private Double precioEntrada; //temporal, precio?
}
