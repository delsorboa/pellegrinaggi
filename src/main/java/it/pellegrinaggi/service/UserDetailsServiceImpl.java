package it.pellegrinaggi.service;


import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.UtenteRepository;


import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;



@Service
public class UserDetailsServiceImpl 
implements UserDetailsService {


private final UtenteRepository repository;


public UserDetailsServiceImpl(
        UtenteRepository repository){

    this.repository = repository;
}



@Override
public UserDetails loadUserByUsername(
        String username)
        throws UsernameNotFoundException {


    Utente utente =
        repository.findByUsername(username)
        .orElseThrow(
            () -> new UsernameNotFoundException(
                "Utente non trovato"
            )
        );


    return User.builder()

        .username(
            utente.getUsername()
        )

        .password(
            utente.getPassword()
        )

        .roles(
            utente.getRuolo().name()
        )

        .disabled(
            !utente.getAttivo()
        )

        .build();

}

}
