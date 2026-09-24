package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEventoRepository
        extends JpaRepository<TipoEvento, Integer> {
}
