package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Scelta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SceltaRepository
        extends JpaRepository<Scelta, Integer> {

//    List<Scelta> findByPellegrinaggioId(
//            Integer pellegrinaggioId
//    );
}
