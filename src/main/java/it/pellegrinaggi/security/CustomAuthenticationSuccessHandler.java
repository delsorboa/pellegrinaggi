package it.pellegrinaggi.security;

import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.UtenteRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

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

        Utente utente = repository.findByUsername(authentication.getName()).orElseThrow();

        if (response.isCommitted()) {
            return;
        }

        // Determina il percorso relativo di destinazione
        String targetPath = "/login";

        if (Boolean.TRUE.equals(utente.getCambioPasswordObbligatorio())) {
            targetPath = "/cambio-password";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            targetPath = "/admin";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTECIPANTE"))) {
            targetPath = "/partecipante";
        }

        // RICOSTRUZIONE URL PUBBLICO PANDASTACK
        String publicRedirectUrl = buildPublicUrl(request, targetPath);

        // Esegue il redirect forzato sull'URL esterno accessibile
        response.sendRedirect(publicRedirectUrl);
    }

    private String buildPublicUrl(HttpServletRequest request, String targetPath) {
        // 1. Prendi l'host reale passato dal proxy o richiesto dal browser
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isEmpty()) {
            host = request.getHeader("Host"); // Es. mia-app.pandastack.ai o localhost:8080
        }

        // 2. Estrai solo la parte dell'host se include la porta (es. localhost:8080 -> localhost)
        if (host != null && host.contains(":")) {
            host = host.split(":")[0];
        }

        // 3. SE l'host è autorizzato, forza HTTPS e ricostruisci l'URL
        if (host != null && isAllowedHost(host)) {
            // Se sei in locale su localhost, puoi mantenere HTTP, altrimenti forza HTTPS
            String scheme = host.equals("localhost") ? "http" : "https";
            String port = host.equals("localhost") ? ":8080" : ""; // Aggiungi la porta solo per il locale se serve
            
            return scheme + "://" + host + port + targetPath;
        }

        // 4. Fallback sicuro all'URL relativo se l'host non è riconosciuto
        return targetPath; 
    }

    private boolean isAllowedHost(String host) {
        return host.equals("localhost") || 
               host.endsWith(".pandastack.ai") || 
               host.endsWith(".tuodominio.it"); // Inserisci i tuoi domini reali
    }

}
