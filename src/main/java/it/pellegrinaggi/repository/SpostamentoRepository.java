package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.Spostamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpostamentoRepository extends JpaRepository<Spostamento, Integer> {

	@Query("""
		    SELECT s
		    FROM Spostamento s
		    JOIN PartecipanteSpostamento ps
		        ON ps.id.idSpostamento = s.id
		    WHERE ps.id.idPartecipante = :idPartecipante
		      AND s.pellegrinaggio.id = :idPellegrinaggio
		    ORDER BY s.dataPartenza
		""")
		List<Spostamento> findByPartecipanteAndPellegrinaggio(
		        @Param("idPartecipante") Integer idPartecipante,
		        @Param("idPellegrinaggio") Integer idPellegrinaggio
		);
	
	
	@Query("""
		    SELECT s
		    FROM Spostamento s
		    WHERE s.pellegrinaggio.id = :idPellegrinaggio
		      AND s.id NOT IN (
		          SELECT ps.id.idSpostamento
		          FROM PartecipanteSpostamento ps
		          WHERE ps.id.idPartecipante = :idPartecipante
		      )
		    ORDER BY s.dataPartenza
		""")
		List<Spostamento> findDisponibiliPerPartecipante(
		        @Param("idPellegrinaggio") Integer idPellegrinaggio,
		        @Param("idPartecipante") Integer idPartecipante
		);

	
    List<Spostamento> findByPellegrinaggio_id(
            Integer idPellegrinaggio
    );

}

