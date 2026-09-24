package it.pellegrinaggi.repository;


import it.pellegrinaggi.model.Partecipante;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PartecipanteRepository
extends JpaRepository<Partecipante,Integer>{
	
	boolean existsByCodiceFiscaleAndIdNot(
	        String codiceFiscale,
	        Integer id
	);
	
	Optional<Partecipante> findById(Integer id);
	
	 List<Partecipante>
	    findByAdesioniPellegrinaggioId(
	            Integer idPellegrinaggio
	    );
	 
	 
	 @Query("""
			    SELECT DISTINCT p
			    FROM Partecipante p
			    JOIN p.adesioni a
			    WHERE a.pellegrinaggio.id = :idPellegrinaggio
			      AND p.id <> :idPartecipante
			""")
			List<Partecipante> findByAdesioniPellegrinaggioIdExcludingPartecipante(
			        @Param("idPellegrinaggio") Integer idPellegrinaggio,
			        @Param("idPartecipante") Integer idPartecipante
			);


}
