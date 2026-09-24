package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Sistemazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SistemazioneRepository
        extends JpaRepository<Sistemazione, Integer> {

	 List<Sistemazione> findByIdPellegrinaggio(Integer idPellegrinaggio);
}

