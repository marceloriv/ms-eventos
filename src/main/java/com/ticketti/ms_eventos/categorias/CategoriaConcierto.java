package com.ticketti.ms_eventos.categorias;

import org.springframework.stereotype.Service;

@Service("concierto")
public class CategoriaConcierto implements Categoria {

    @Override
    public String mandarMensaje(String mensaje) {
        return "Mensaje de categoría concierto " + mensaje;
    }
}
