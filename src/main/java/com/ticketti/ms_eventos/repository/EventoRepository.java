package com.ticketti.ms_eventos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.model.Genero;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
        // PARA EL MÉTODO BUSCAR, YA QUE TIENE UNA RELACIÓN CON RECINTO, Y SE NECESITA
        // LA UBICACIÓN
        // ESTE MÉTODO BUSCA POR: GÉNERO, NOMBRE, CATEGORÍA, UBICACIÓN.
        @Query("SELECT e FROM Evento e WHERE " +
                        "(:genero IS NULL OR e.genero = :genero) AND " +
                        "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
                        "(:ubicacion IS NULL OR LOWER(e.recinto.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%')))")
        List<Evento> buscarPorFiltros(
                        @Param("genero") Genero genero,
                        @Param("nombre") String nombre,
                        @Param("ubicacion") String ubicacion);
}
