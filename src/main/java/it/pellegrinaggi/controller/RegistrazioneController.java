package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.*;
import it.pellegrinaggi.repository.*;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class RegistrazioneController {



private final UtenteRepository utenteRepository;

private final BCryptPasswordEncoder encoder;



public RegistrazioneController(
        UtenteRepository utenteRepository,
        BCryptPasswordEncoder encoder){

    this.utenteRepository=utenteRepository;
    this.encoder=encoder;
}




@GetMapping("/registrazione")
public String pagina(){

    return "registrazione";

}




@PostMapping("/registrazione")
public String registra(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam String nome,
        @RequestParam String cognome,
        @RequestParam String email){



    Partecipante p =
            new Partecipante();


    p.setNome(nome);

    p.setCognome(cognome);

    p.setEmail(email);



    Utente u =
            new Utente();


    u.setUsername(username);

    u.setPassword(
        encoder.encode(password)
    );


    u.setRuolo(
        Ruolo.PARTECIPANTE
    );


    u.setPartecipante(p);



    utenteRepository.save(u);



    return "redirect:/login";

}


}