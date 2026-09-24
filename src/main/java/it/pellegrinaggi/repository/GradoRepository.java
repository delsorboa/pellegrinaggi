package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Grado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradoRepository
        extends JpaRepository<Grado, Integer> {
}