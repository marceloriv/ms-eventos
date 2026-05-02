package com.ticketti.ms_eventos.dto;

import java.util.Date;

import com.ticketti.ms_eventos.model.Estado;
import com.ticketti.ms_eventos.model.Genero;
import com.ticketti.ms_eventos.model.Recinto;

import lombok.Data;

@Data
public class EventoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Date fecha;
    private Genero genero;
    private Recinto recinto;
    private Estado estado;
    private Double precioEntrada;
}
