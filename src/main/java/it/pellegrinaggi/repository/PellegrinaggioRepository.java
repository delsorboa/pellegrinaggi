package it.pellegrinaggi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.StatoPellegrinaggio;

public interface PellegrinaggioRepository
extends JpaRepository<Pellegrinaggio,Integer> {
	
	List<Pellegrinaggio> findByStato(
	        StatoPellegrinaggio stato);
	
	// Metodo per la ricerca parziale e case-insensitive del nome con paginazione
    Page<Pellegrinaggio> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}
