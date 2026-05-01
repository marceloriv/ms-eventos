package com.ticketti.ms_eventos.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ticketti.ms_eventos.categorias.Categoria;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoriaFactory {
    private final Map<String, Categoria> tiposCategorias;

    public Categoria obtenerCategoria(String tipo) {
        Categoria cat = tiposCategorias.get(tipo.toLowerCase());

        if (cat == null) {
            throw new IllegalArgumentException("No extiste una categoria del tipo x");
        }

        return cat;

    }
}
