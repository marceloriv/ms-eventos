package com.ticketti.ms_eventos.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

    @Column(length = 255)
    private String imagenUrl;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String nombre;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Date fecha;

    // enum género para concierto, cine móvil y festival cultural}
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Genero genero;

    /**
     * @OneToOne(fetch = FetchType.EAGER) // Recinto siempre junto con Evento,
     *                 se agrega ya que hibernate por defecto carga las relaciones
     *                 de forma lazy, lo
     *                 que es,
     *                 no carga hasta que realmente le pides que lo haga con EAGER.
     *                 también, ya que los JSON se harán con recinto dentro, este
     *                 tiene que correr
     *                 en cascada, porque sino, hibernate corre evento sin primero
     *                 ir a guardar un recinto.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Recinto recinto;
    // private Recinto recinto;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Estado estado;

    /**
     * fix general: validaciones de valores en blanco,
     * not null para números,
     * y no se pueden poner valores numericos negativos.
     */

    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer aforo;

    @Column(nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer stock;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Double precioEntrada;
}
