package com.ticketti.ms_eventos.categorias;

import org.springframework.stereotype.Service;

@Service("festivalcultural")
public class CategoriaFestival implements Categoria {

    @Override
    public String mandarMensaje(String mensaje) {
        return "Mensaje de categoría festival cultural " + mensaje;
    }
}
