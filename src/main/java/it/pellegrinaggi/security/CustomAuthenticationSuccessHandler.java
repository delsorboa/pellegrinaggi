package it.pellegrinaggi.security;

import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.UtenteRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UtenteRepository repository;
    
    // STRATEGIA DI REDIRECT DI SPRING: Gestisce correttamente i proxy come PandaStack
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public CustomAuthenticationSuccessHandler(UtenteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        Utente utente = repository.findByUsername(authentication.getName()).orElseThrow();

        // Se la risposta è già stata inviata al client, interrompi
        if (response.isCommitted()) {
            return;
        }

        // 1. Primo accesso: cambio password obbligatorio
        if (Boolean.TRUE.equals(utente.getCambioPasswordObbligatorio())) {
            redirectStrategy.sendRedirect(request, response, "/cambio-password");
            return;
        }

        // 2. Amministratore
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectStrategy.sendRedirect(request, response, "/admin");
            return;
        }

        // 3. Partecipante
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTECIPANTE"))) {
            redirectStrategy.sendRedirect(request, response, "/partecipante");
            return;
        }

        // Fallback di sicurezza
        redirectStrategy.sendRedirect(request, response, "/login");
    }
}
