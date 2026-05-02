package com.ticketti.ms_eventos.dto;

import java.util.Date;

import com.ticketti.ms_eventos.model.Genero;

import lombok.Data;

@Data
public class EventoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Date fecha;
    private Genero genero;
    private String recinto;
    private Double precioEntrada;
}
