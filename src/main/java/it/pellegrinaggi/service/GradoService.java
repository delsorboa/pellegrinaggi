package it.pellegrinaggi.service;

import it.pellegrinaggi.model.Grado;
import it.pellegrinaggi.repository.GradoRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GradoService {

    private final GradoRepository repository;

    public GradoService(GradoRepository repository) {
        this.repository = repository;
    }

    public List<Grado> findAll() {
        return repository.findAll();
    }

    public Grado findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Grado non trovato")
                );
    }

    public Grado save(Grado grado) {
        return repository.save(grado);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}

