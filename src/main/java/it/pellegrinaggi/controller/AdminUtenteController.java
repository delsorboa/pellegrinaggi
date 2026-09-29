package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Ruolo;
import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.PartecipanteRepository;
import it.pellegrinaggi.repository.UtenteRepository;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;


@Controller
@RequestMapping("/admin/utenti")
public class AdminUtenteController {


	  private final UtenteRepository repository;
	    private final PasswordEncoder passwordEncoder;
	    private final PartecipanteRepository partecipanteRepository;

	    // Rimosso RuoloRepository dal costruttore
	    public AdminUtenteController(UtenteRepository repository,
	            PasswordEncoder passwordEncoder,
	            PartecipanteRepository partecipanteRepository) {
	        this.repository = repository;
	        this.passwordEncoder = passwordEncoder;
	        this.partecipanteRepository = partecipanteRepository;
	    }


	    @GetMapping
	    public String elenco(
	            @RequestParam(name = "username", required = false) String username,
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "10") int size,
	            Model model) {

	        // Evita pagine negative
	        if (page < 0) {
	            page = 0;
	        }

	     // Configurazione della paginazione ordinata per il Partecipante collegato
	        Pageable pageable = PageRequest.of(
	                page,
	                size,
	                Sort.by(
	                        Sort.Order.asc("partecipante.cognome"),
	                        Sort.Order.asc("partecipante.nome")
	                )
	        );


	        Page<Utente> paginaUtenti;

	        // Se il filtro dello username è valorizzato, effettua la ricerca parziale
	        if (username != null && !username.isBlank()) {
	            paginaUtenti = repository.findByUsernameContainingIgnoreCase(username.trim(), pageable);
	        } 
	        // Altrimenti estrae l'elenco completo paginato
	        else {
	            paginaUtenti = repository.findAll(pageable);
	        }

	        // Passiamo alla tabella HTML solo la lista scompattata degli elementi della pagina corrente
	        model.addAttribute("utenti", paginaUtenti.getContent());
	        
	        // Parametri fondamentali per far funzionare i bottoni della paginazione nell'HTML
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", paginaUtenti.getTotalPages());
	        model.addAttribute("totalItems", paginaUtenti.getTotalElements());

	        // Mantiene lo username digitato all'interno del campo di testo dell'HTML
	        model.addAttribute("username", username);

	        return "admin/utenti/elenco";
	    }




    @GetMapping("/abilita/{id}")
    public String abilita(
            @PathVariable Integer id){


        Utente utente =
                repository.findById(id)
                .orElseThrow();


        utente.setAttivo(true);


        repository.save(utente);


        return "redirect:/admin/utenti";

    }



    @GetMapping("/disabilita/{id}")
    public String disabilita(
            @PathVariable Integer id){


        Utente utente =
                repository.findById(id)
                .orElseThrow();


        utente.setAttivo(false);


        repository.save(utente);


        return "redirect:/admin/utenti";

    }




    @GetMapping("/modifica-password/{id}")
    public String modificaPassword(@PathVariable Integer id, Model model) {

        Optional<Utente> optionalUtente = repository.findById(id);

        if (optionalUtente.isEmpty()) {
            return "redirect:/admin/utenti";
        }

        Utente utente = optionalUtente.get();

        model.addAttribute("utente", utente);

        return "admin/utenti/modifica-password";
    }
    
    
    @PostMapping("/modifica-password/{id}")
    public String salvaNuovaPassword(
            @PathVariable Integer id,
            @RequestParam String password,
            @RequestParam String confermaPassword,
            Model model) {

        Utente utente = repository.findById(id).orElse(null);

        if (utente == null) {
            return "redirect:/admin/utenti";
        }

        // Controllo password vuota
        if (password == null || password.isBlank()) {

            model.addAttribute("utente", utente);
            model.addAttribute("errore", "La password non può essere vuota.");

            return "admin/modifica-password";
        }

        // Controllo corrispondenza password
        if (!password.equals(confermaPassword)) {

            model.addAttribute("utente", utente);
            model.addAttribute("errore", "Le password non coincidono.");

            return "admin/modifica-password";
        }

        // Criptazione BCrypt
        String passwordCriptata = passwordEncoder.encode(password);

        // Salvataggio nel campo password
        utente.setPassword(passwordCriptata);

        repository.save(utente);

        return "redirect:/admin/utenti";
    }
    
    
    @GetMapping("/form")
    public String mostraForm(Model model) {
        // 1. Passa un oggetto utente vuoto per il th:object
        model.addAttribute("utente", new Utente()); 
        
        // 2. Carica le liste necessarie per popolare le due combo
        model.addAttribute("partecipanti", partecipanteRepository.findAll());
        model.addAttribute("ruoli", Ruolo.values()); 
        
        return "admin/utenti/form"; // Assicurati che il percorso del template sia corretto
    }
    
    @PostMapping("/salva")
    public String salva(
            @ModelAttribute("utente") Utente utente,
            @RequestParam("partecipanteId") Integer partecipanteId,
            @RequestParam("ruolo") Ruolo ruolo,
            Model model) {

        // 1. VERIFICA SE IL PARTECIPANTE HA GIÀ UN'UTENZA
        Optional<Utente> utenteGiaEsistente = repository.findByPartecipanteId(partecipanteId);

        if (utenteGiaEsistente.isPresent()) {
            Utente trovato = utenteGiaEsistente.get();
            
            // Se l'ID è nullo (nuovo inserimento), oppure se l'ID trovato è DIVERSO da quello che stiamo modificando
            if (utente.getId() == null || !trovato.getId().equals(utente.getId())) {
                
                // Blocca il salvataggio e rimanda al form mostrando l'errore
                model.addAttribute("utente", utente);
                model.addAttribute("partecipanti", partecipanteRepository.findAll());
                model.addAttribute("ruoli", Ruolo.values());
                model.addAttribute("errore", "A questo partecipante è già stata assegnata un'utenza!");
                
                return "admin/utenti/form";
            }
        }
        
        // 1.B VERIFICA SE LO USERNAME È GIÀ IN USO DA UN ALTRO UTENTE
        Optional<Utente> utenteConStessoUsername = repository.findByUsername(utente.getUsername());

        if (utenteConStessoUsername.isPresent()) {
            Utente trovatoUsername = utenteConStessoUsername.get();

            // Se stiamo creando un nuovo utente, o se l'utente trovato ha un ID diverso da quello corrente
            if (utente.getId() == null || !trovatoUsername.getId().equals(utente.getId())) {
                
                model.addAttribute("utente", utente);
                model.addAttribute("partecipanti", partecipanteRepository.findAll());
                model.addAttribute("ruoli", Ruolo.values());
                model.addAttribute("errore", "Questo username è già in uso! Scegline un altro.");
                
                return "admin/utenti/form";
            }
        }

        // 2. GESTIONE INSERIMENTO (INSERT) O MODIFICA (UPDATE)
        if (utente.getId() == null) {
            // ========================================================
            // NUOVO UTENTE (INSERT) con max(id) + 1
            // ========================================================
            
            // Calcolo manuale dell'ID massimo + 1
            Integer nextId = repository.findMaxId() + 1;
            utente.setId(nextId);
            
            utente.setAttivo(true);
            if (utente.getPassword() != null && !utente.getPassword().isBlank()) {
                utente.setPassword(passwordEncoder.encode(utente.getPassword()));
            }
            
            utente.setPartecipante(partecipanteRepository.findById(partecipanteId).orElse(null));
            utente.setRuolo(ruolo);
            
            repository.save(utente);
        } else {
            // ========================================================
            // UTENTE ESISTENTE (UPDATE sicuro)
            // ========================================================
            Utente utenteEsistente = repository.findById(utente.getId()).orElseThrow();
            
            utenteEsistente.setUsername(utente.getUsername());
            utenteEsistente.setRuolo(ruolo);
            utenteEsistente.setPartecipante(partecipanteRepository.findById(partecipanteId).orElse(null));
            
            if (utente.getPassword() != null && !utente.getPassword().isBlank()) {
                utenteEsistente.setPassword(passwordEncoder.encode(utente.getPassword()));
            }

            repository.save(utenteEsistente);
        }

        return "redirect:/admin/utenti";
    }

    

}
