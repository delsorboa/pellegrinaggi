package it.pellegrinaggi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.pellegrinaggi.model.Spostamento;
import it.pellegrinaggi.repository.QualificaRepository;
import it.pellegrinaggi.repository.SpostamentoRepository;
import lombok.RequiredArgsConstructor;

@Service
public class SpostamentoService {

    private final SpostamentoRepository spostamentoRepository;
    
    
    public SpostamentoService(SpostamentoRepository repository) {
        this.spostamentoRepository = repository;
    }

    /**
     * Recupera tutti gli spostamenti appartenenti
     * ad un determinato pellegrinaggio.
     */
    @Transactional(readOnly = true)
    public List<Spostamento> findByPellegrinaggio(Integer idPellegrinaggio) {

        return spostamentoRepository
                .findByPellegrinaggio_id(idPellegrinaggio);
    }

    /**
     * Recupera tutti gli spostamenti associati
     * ad un determinato partecipante.
     */
    @Transactional(readOnly = true)
    public List<Spostamento> findByPartecipanteAndPellegrinaggio(Integer idPartecipante,Integer idPellegrinaggio) {

        return spostamentoRepository
                .findByPartecipanteAndPellegrinaggio(idPartecipante,idPellegrinaggio);
    }
    
    @Transactional(readOnly = true)
    public List<Spostamento> findDisponibiliPerPartecipante(Integer idPelegrinaggio,Integer idPellegrinaggio) {

        return spostamentoRepository
                .findDisponibiliPerPartecipante(idPelegrinaggio,idPellegrinaggio);
    }


    /**
     * Recupera uno spostamento per ID.
     */
    @Transactional(readOnly = true)
    public Spostamento findById(Integer id) {

        return spostamentoRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Spostamento non trovato: " + id
                    )
                );
    }
}
