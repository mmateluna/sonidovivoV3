package cl.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Microservicio de catálogo. Lee el JWT emitido por auth, pero no guarda usuarios.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CatalogoApplication {

  public static void main(String[] args) {
    SpringApplication.run(CatalogoApplication.class, args);
  }
}
