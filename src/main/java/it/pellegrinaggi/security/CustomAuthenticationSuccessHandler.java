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
            // 1. Controlla prima di tutto l'host originale richiesto dal browser (Header standard dei proxy)
            String host = request.getHeader("X-Forwarded-Host");
            
            // 2. Se è assente, prova a leggere l'header Host standard
            if (host == null || host.isEmpty()) {
                host = request.getHeader("Host");
            }

            // PROTECTION CONTRO GLI IP PRIVATI: Se l'host estratto è un IP interno (es. inizia con 10. o 172. o 192.) 
            // o contiene porte interne (8081, 8080), facciamo fallback sugli header di navigazione sicuri
            if (host != null && (host.startsWith("10.") || host.startsWith("172.") || host.startsWith("192.") || host.contains(":8081"))) {
                // Estrae il dominio pubblico dall'Origin o dal Referer inviato dal browser durante il click su "Entra"
                String originHeader = request.getHeader("origin");
                if (originHeader == null || originHeader.isEmpty()) {
                    originHeader = request.getHeader("referer");
                }
                if (originHeader != null && !originHeader.isEmpty()) {
                    java.net.URI uri = new java.net.URI(originHeader);
                    host = uri.getHost(); // Recupera es. 9a9da55f-bdb7-4744-b7a5-fb5fe7ac63be.db.pandastack.ai
                }
            }

            // 3. Se abbiamo un host valido e non è un IP privato, ricostruiamo l'URL pubblico in HTTPS
            if (host != null && !host.isEmpty() && !host.startsWith("10.")) {
                // Rimuove eventuali porte residue attaccate all'host
                if (host.contains(":")) {
                    host = host.split(":")[0];
                }
                
                String scheme = "https"; // Forza HTTPS per l'esterno su PandaStack
                return scheme + "://" + host + targetPath;
            }
        } catch (Exception e) {
            System.out.println("Errore nella generazione dell'URL pubblico: " + e.getMessage());
        }
        
        // Fallback relativo se non riusciamo a calcolare l'URL assoluto
        return request.getContextPath() + targetPath;
    }

}
