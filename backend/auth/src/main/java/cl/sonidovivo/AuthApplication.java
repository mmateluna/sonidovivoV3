package cl.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Login, registro y renovación del JWT. Las cuentas viven en el servicio de usuarios.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class AuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthApplication.class, args);
  }
}
