package cl.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Mensajes del formulario de contacto.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ContactoApplication {

  public static void main(String[] args) {
    SpringApplication.run(ContactoApplication.class, args);
  }
}
