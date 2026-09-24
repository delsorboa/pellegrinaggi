package it.pellegrinaggi.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratorePassword {

    public static void main(String[] args) {
        // Inserisci qui la tua password in chiaro
        String passwordInChiaro = "admin"; 
        
        // Inizializza l'encoder standard di Spring Security (strength di default = 10)
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Genera l'hash criptato
        String passwordCriptata = encoder.encode(passwordInChiaro);
        
        // Stampa i risultati in console
        System.out.println("==================================================");
        System.out.println("Password in chiaro: " + passwordInChiaro);
        System.out.println("Hash BCrypt esatto: " + passwordCriptata);
        System.out.println("==================================================");
    }
}