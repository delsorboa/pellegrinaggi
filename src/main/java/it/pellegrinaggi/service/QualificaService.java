package it.pellegrinaggi.service;

import it.pellegrinaggi.model.Qualifica;
import it.pellegrinaggi.repository.QualificaRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class QualificaService {

    private final QualificaRepository repository;

    public QualificaService(QualificaRepository repository) {
        this.repository = repository;
    }

    public List<Qualifica> findAll() {
        return repository.findAll();
    }

    public Qualifica findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Qualifica non trovata")
                );
    }

    public Qualifica save(Qualifica qualifica) {
        return repository.save(qualifica);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}

