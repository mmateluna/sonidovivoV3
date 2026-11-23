package cl.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Cuentas de la tienda: registro interno, listado del administrador y validación de clave.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class UsuariosApplication {

  public static void main(String[] args) {
    SpringApplication.run(UsuariosApplication.class, args);
  }
}
