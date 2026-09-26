package it.pellegrinaggi.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import it.pellegrinaggi.repository.UtenteRepository; // Assicurati che il package sia corretto
import it.pellegrinaggi.model.Utente;             // Assicurati che il package sia corretto

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UtenteRepository repository;

    public CustomAuthenticationSuccessHandler(UtenteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (response.isCommitted()) {
            return;
        }

        // 1. Lettura sicura dal DB per evitare NoSuchElementException
        Optional<Utente> utenteOpt = repository.findByUsername(authentication.getName());
        if (utenteOpt.isEmpty()) {
            System.err.println("Utente autenticato ma non presente nel DB: " + authentication.getName());
            response.sendRedirect(buildPublicUrl(request, "/login?error"));
            return;
        }

        Utente utente = utenteOpt.get();
        String targetPath = "/login";

        // Determina il percorso di destinazione
        if (Boolean.TRUE.equals(utente.getCambioPasswordObbligatorio())) {
            targetPath = "/cambio-password";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            targetPath = "/admin";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTECIPANTE"))) {
            targetPath = "/partecipante";
        }

        // 2. Costruzione sicura dell'URL pubblico assoluto
        String publicRedirectUrl = buildPublicUrl(request, targetPath);
        
        // Log di debug visibile nella console di PandaStack per verificare cosa viene generato
        System.out.println("Redirect di successo generato verso l'URL: " + publicRedirectUrl);
        
        response.sendRedirect(publicRedirectUrl);
    }

    private String buildPublicUrl(HttpServletRequest request, String targetPath) {
        try {
            // Sfrutta server.forward-headers-strategy=framework per leggere l'host reale da Cloudflare/PandaStack
            return ServletUriComponentsBuilder.fromContextPath(request)
                    .scheme("https") // Forza HTTPS richiesto da PandaStack
                    .replacePath(targetPath)
                    .toUriString();
        } catch (Exception e) {
            System.err.println("Errore nella generazione dell'URL pubblico, uso fallback relativo: " + e.getMessage());
            return targetPath;
        }
    }
}
