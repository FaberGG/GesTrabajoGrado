package com.unicauca.identity.enums;
package com.unicauca.identity;
/**
 * Enum que define los programas académicos disponibles en el sistema
 */
public enum Programa {
    INGENIERIA_DE_SISTEMAS,
    INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES,
    AUTOMATICA_INDUSTRIAL,
    TECNOLOGIA_EN_TELEMATICA
}

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal del microservicio de identidad
 */
@SpringBootApplication
@EnableJpaAuditing
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}
