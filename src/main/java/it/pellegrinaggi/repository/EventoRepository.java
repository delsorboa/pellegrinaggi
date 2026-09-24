package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Eventi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventoRepository
        extends JpaRepository<Eventi, Integer> {

    List<Eventi> findByIdPellegrinaggio(Integer idPellegrinaggio);
    
    List<Eventi> findByTipoEvento_IdAndIdPellegrinaggio(
            Integer idTipoEvento,
            Integer idPellegrinaggio
    );
    
    @Query("""
    	    SELECT e
    	    FROM Eventi e
    	    WHERE e.idPellegrinaggio = :idPellegrinaggio
    	      AND NOT EXISTS (
    	          SELECT pe
    	          FROM PartecipanteEvento pe
    	          WHERE pe.evento.id = e.id
    	            AND pe.idPartecipante = :idPartecipante
    	      )
    	    ORDER BY e.descrizione
    	""")
    	List<Eventi> findEventiDisponibili(
    	        @Param("idPellegrinaggio") Integer idPellegrinaggio,
    	        @Param("idPartecipante") Integer idPartecipante
    	);


}

