package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Qualifica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualificaRepository
        extends JpaRepository<Qualifica, Integer> {
}
