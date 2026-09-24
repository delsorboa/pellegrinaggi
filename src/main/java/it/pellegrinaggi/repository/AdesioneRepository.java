package it.pellegrinaggi.repository;


import it.pellegrinaggi.model.Adesione;
import it.pellegrinaggi.model.Partecipante;
import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.Utente;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AdesioneRepository 
        extends JpaRepository<Adesione,Integer> {


    boolean existsByPartecipanteAndPellegrinaggio(
            Partecipante partecipante,
            Pellegrinaggio pellegrinaggio
    );


    long countByPellegrinaggio(
            Pellegrinaggio pellegrinaggio
    );
    
    List<Adesione> findByPartecipante(
            Partecipante partecipante);
    
    long countByPellegrinaggioId(Integer id);
    
    List<Adesione> findByUtenteId(Integer utenteId);
    
    
    boolean existsByPartecipanteAndPellegrinaggioAndIdNot(
            Partecipante partecipante,
            Pellegrinaggio pellegrinaggio,
            Integer id
    );
    
    @Query("""
    	    SELECT a
    	    FROM Adesione a
    	    WHERE
    	        (
    	            :idPellegrinaggio IS NULL
    	            OR a.pellegrinaggio.id = :idPellegrinaggio
    	        )
    	        AND
    	        (
    	            :nome IS NULL
    	            OR :nome = ''
    	            OR LOWER(a.partecipante.nome)
    	                LIKE LOWER(CONCAT('%', :nome, '%'))
    	            OR LOWER(a.partecipante.cognome)
    	                LIKE LOWER(CONCAT('%', :nome, '%'))
    	        )
    	    ORDER BY
    	        a.partecipante.cognome,
    	        a.partecipante.nome
    	""")
    	Page<Adesione> cerca(
    	        @Param("idPellegrinaggio") Integer idPellegrinaggio,
    	        @Param("nome") String nome,
    	        Pageable pageable
    	);

    
    

}
