package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.PartecipanteSistemazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartecipanteSistemazioneRepository
        extends JpaRepository<PartecipanteSistemazione, Integer> {

    /*
     * Tutte le sistemazioni di un partecipante.
     */
    List<PartecipanteSistemazione>
    findByPartecipanteId(Integer partecipanteId);


    /*
     * Sistemazioni di un partecipante
     * all'interno di uno specifico pellegrinaggio.
     */
    List<PartecipanteSistemazione>
    findByPartecipanteIdAndCameraSistemazioneIdPellegrinaggio(
            Integer partecipanteId,
            Integer idPellegrinaggio
    );


    /*
     * Cerca una specifica associazione
     * partecipante + camera.
     */
    Optional<PartecipanteSistemazione>
    findByPartecipanteIdAndCameraId(
            Integer partecipanteId,
            Integer cameraId
    );


    /*
     * Cerca se il partecipante è già nella
     * camera indicata nello specifico pellegrinaggio.
     */
    Optional<PartecipanteSistemazione>
    findByPartecipanteIdAndCameraIdAndCameraSistemazioneIdPellegrinaggio(
            Integer partecipanteId,
            Integer cameraId,
            Integer idPellegrinaggio
    );


    /*
     * Tutti i partecipanti presenti in una camera.
     */
    List<PartecipanteSistemazione>
    findByCameraId(Integer cameraId);


    /*
     * Tutti i partecipanti presenti nella camera
     * nello specifico pellegrinaggio.
     */
    List<PartecipanteSistemazione>
    findByCameraIdAndCameraSistemazioneIdPellegrinaggio(
            Integer cameraId,
            Integer idPellegrinaggio
    );
    
    
    @Query("""
    	    SELECT ps
    	    FROM PartecipanteSistemazione ps
    	    WHERE ps.partecipante.id = :idPartecipante
    	      AND ps.camera.sistemazione.idPellegrinaggio = :idPellegrinaggio
    	""")
    	List<PartecipanteSistemazione> findByPartecipanteAndPellegrinaggio(
    	        @Param("idPartecipante") Integer idPartecipante,
    	        @Param("idPellegrinaggio") Integer idPellegrinaggio
    	);



}


