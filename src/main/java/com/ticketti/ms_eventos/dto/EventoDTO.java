package com.ticketti.ms_eventos.dto;

import java.util.Date;

import lombok.Data;

@Data
public class EventoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Date fecha;
    private String categoria;
    private String recinto;
    private Double precioEntrada;
}
