package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Tabella;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TabellaRepository
        extends JpaRepository<Tabella, Integer> {
}

