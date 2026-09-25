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

        // LOG 1: Controlliamo l'URL che il server pensa di avere
        System.out.println("=== DEBUG PANDASTACK START ===");
        System.out.println("Request URL: " + request.getRequestURL().toString());
        System.out.println("Scheme (HTTP/HTTPS): " + request.getScheme());
        System.out.println("Server Name: " + request.getServerName());
        System.out.println("Server Port: " + request.getServerPort());

        // LOG 2: Stampiamo tutti gli header inviati da PandaStack
        System.out.println("--- Richiesta Header Ricevuti ---");
        java.util.Collections.list(request.getHeaderNames()).forEach(headerName -> {
            System.out.println(headerName + ": " + request.getHeader(headerName));
        });
        System.out.println("=== DEBUG PANDASTACK END ===");

        Utente utente = repository.findByUsername(authentication.getName()).orElseThrow();

        if (response.isCommitted()) {
            return;
        }

        if (Boolean.TRUE.equals(utente.getCambioPasswordObbligatorio())) {
            redirectStrategy.sendRedirect(request, response, "/cambio-password");
            return;
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectStrategy.sendRedirect(request, response, "/admin");
            return;
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTECIPANTE"))) {
            redirectStrategy.sendRedirect(request, response, "/partecipante");
            return;
        }

        redirectStrategy.sendRedirect(request, response, "/login");
    }

}
