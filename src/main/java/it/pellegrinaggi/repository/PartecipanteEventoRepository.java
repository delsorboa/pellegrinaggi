package it.pellegrinaggi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query("SELECT DISTINCT pe FROM PartecipanteEvento pe " +
    	       "JOIN pe.evento e " +
    	       "WHERE pe.idPellegrinaggio = :idPellegrinaggio " +
    	       "AND pe.idPartecipante = :idPartecipante " +
    	       "AND e.eventoPadre IS NULL")
    	List<PartecipanteEvento> findEventiRadicePerPartecipante(
    	        @Param("idPellegrinaggio") Integer idPellegrinaggio,
    	        @Param("idPartecipante") Integer idPartecipante
    	);
    
    
    @Query("SELECT pe FROM PartecipanteEvento pe " +
            "JOIN pe.evento e " +
            "WHERE e.eventoPadre.id = :idPadre " +
            "AND pe.idPartecipante = :idPartecipante " +
            "AND pe.idPellegrinaggio = :idPellegrinaggio")
     List<PartecipanteEvento> findFigliByIdPadreEPartecipante(
             @Param("idPadre") Integer idPadre, 
             @Param("idPartecipante") Integer idPartecipante,
             @Param("idPellegrinaggio") Integer idPellegrinaggio
     );

}

