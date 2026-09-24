package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CameraRepository extends JpaRepository<Camera, Integer> {

    List<Camera> findBySistemazioneId(Integer sistemazioneId);
    
    Optional<Camera> findBySistemazioneIdAndDescrizione(
            Integer idSistemazione,
            String descrizione
    );

}

