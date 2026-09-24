package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Servizio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServizioRepository
        extends JpaRepository<Servizio, Integer> {

    List<Servizio> findByIdPellegrinaggio(
            Integer idPellegrinaggio
    );
    
    Optional<Servizio> findByIdAndIdPellegrinaggio(
            Integer id,
            Integer idPellegrinaggio
    );
    
}
