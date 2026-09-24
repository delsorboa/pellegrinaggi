package it.pellegrinaggi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.pellegrinaggi.model.PartecipanteEvento;

public interface PartecipanteEventoRepository
        extends JpaRepository<PartecipanteEvento, Integer> {

    List<PartecipanteEvento> findByIdPartecipanteAndIdPellegrinaggio(
            Integer idPartecipante,
            Integer idPellegrinaggio
    );

    Optional<PartecipanteEvento> findByIdPartecipanteAndEvento_Id(
            Integer idPartecipante,
            Integer idEvento
    );

    List<PartecipanteEvento>
    findByEvento_IdAndIdPellegrinaggioAndRuolo_Id(
            Integer idEvento,
            Integer idPellegrinaggio,
            Integer idRuolo
    );
    
    List<PartecipanteEvento> findByIdPellegrinaggioAndEvento_Id(
            Integer idPellegrinaggio,
            Integer idEvento
    );
}

