package it.pellegrinaggi.repository;

import it.pellegrinaggi.model.ResponsabileSpostamento;
import it.pellegrinaggi.model.ResponsabileSpostamentoId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponsabileSpostamentoRepository
        extends JpaRepository<
                ResponsabileSpostamento,
                ResponsabileSpostamentoId> {

    List<ResponsabileSpostamento>
    findByIdIdSpostamento(Integer idSpostamento);
}

