package it.pellegrinaggi.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.UtenteRepository;

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

        // 1. SAFE DB LOOKUP: Prevent NoSuchElementException from breaking the gateway
        Optional<Utente> utenteOpt = repository.findByUsername(authentication.getName());
        if (utenteOpt.isEmpty()) {
            System.err.println("Autenticazione riuscita ma utente non trovato nel DB: " + authentication.getName());
            response.sendRedirect(buildPublicUrl(request, "/login?error=usernotfound"));
            return;
        }

        Utente utente = utenteOpt.get();
        String targetPath = "/login";

        // Determine destination path
        if (Boolean.TRUE.equals(utente.getCambioPasswordObbligatorio())) {
            targetPath = "/cambio-password";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            targetPath = "/admin";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTECIPANTE"))) {
            targetPath = "/partecipante";
        }

        // 2. SAFE URL REDIRECT
        String publicRedirectUrl = buildPublicUrl(request, targetPath);
        response.sendRedirect(publicRedirectUrl);
    }

    private String buildPublicUrl(HttpServletRequest request, String targetPath) {
        try {
            // Using standard Spring utilities automatically respects X-Forwarded-* headers 
            // sent by PandaStack / Cloudflare proxies seamlessly.
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .scheme("https") // Force HTTPS for production
                    .replacePath(targetPath)
                    .toUriString();
        } catch (Exception e) {
            System.err.println("Errore nella generazione dell'URL pubblico: " + e.getMessage());
            return targetPath; // Fallback to relative
        }
    }
}
