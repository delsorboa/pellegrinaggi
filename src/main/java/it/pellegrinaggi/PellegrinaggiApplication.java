package it.pellegrinaggi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class PellegrinaggiApplication {

    public static void main(String[] args) {

    	 // 2. AVVIO DI SPRING BOOT
        ApplicationContext context =  SpringApplication.run(
            PellegrinaggiApplication.class,
            args
        );
        
     // 3. ISPEZIONE DEI PARAMETRI FINALI EFFETTIVAMENTE CARICATI DA SPRING
        Environment env = context.getBean(Environment.class);
        
        System.out.println("\n=========================================================");
        System.out.println("=== PARAMETRI EFFETTIVI DI APPLICATION.PROPERTIES ===");
        System.out.println("=========================================================");
        System.out.println("Server Port: " + env.getProperty("server.port"));
        System.out.println("Driver Class: " + env.getProperty("spring.datasource.driver-class-name"));
        System.out.println("JDBC URL: " + env.getProperty("spring.datasource.url"));
        System.out.println("DB Username: " + env.getProperty("spring.datasource.username"));
        System.out.println("JPA Database Platform: " + env.getProperty("spring.jpa.database-platform"));
        System.out.println("Hibernate Dialect: " + env.getProperty("spring.jpa.properties.hibernate.dialect"));
        System.out.println("=========================================================");
    }
}
