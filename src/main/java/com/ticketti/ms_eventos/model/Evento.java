package com.ticketti.ms_eventos.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Date fecha;

    // enum género para concierto, cine móvil y festival cultural}
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Genero genero;

    // @Column(nullable= false, length= 50)
    // private String categoria; //factory method

    @OneToOne
    private Recinto recinto;

    @Column(nullable = false)
    private Integer aforo; // temporal

    @Column(nullable = false)
    private Integer stock; // temporal

    @Column(nullable = false)
    private Double precioEntrada;
}
