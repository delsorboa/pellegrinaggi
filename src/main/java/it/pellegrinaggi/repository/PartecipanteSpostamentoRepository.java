package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.PartecipanteSpostamento;
import it.pellegrinaggi.model.PartecipanteSpostamentoId;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


	public interface PartecipanteSpostamentoRepository
	        extends JpaRepository<
	                PartecipanteSpostamento,
	                PartecipanteSpostamentoId> {

	    List<PartecipanteSpostamento>
	    findByIdIdPartecipante(Integer idPartecipante);

	    Optional<PartecipanteSpostamento>
	    findByIdIdPartecipanteAndIdIdSpostamento(
	            Integer idPartecipante,
	            Integer idSpostamento
	    );

	    boolean existsByIdIdPartecipanteAndIdIdSpostamento(
	            Integer idPartecipante,
	            Integer idSpostamento
	    );

	    void deleteByIdIdPartecipanteAndIdIdSpostamento(
	            Integer idPartecipante,
	            Integer idSpostamento
	    );
	}



