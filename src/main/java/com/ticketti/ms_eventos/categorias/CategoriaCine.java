package com.ticketti.ms_eventos.categorias;

import org.springframework.stereotype.Service;

@Service("cinemovil")
public class CategoriaCine implements Categoria {

    @Override
    public String mandarMensaje(String mensaje) {
        return "Mensaje de categoría cine móvil " + mensaje;
    }
}
