package it.pellegrinaggi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.pellegrinaggi.model.TipViaggio;

public interface TipViaggioRepository extends JpaRepository<TipViaggio, Integer> {
}

