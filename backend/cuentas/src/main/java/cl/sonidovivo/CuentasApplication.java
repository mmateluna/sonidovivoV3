package cl.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Microservicio de cuentas: login, registro, renovación y usuarios.
// El panel de administración no es otro proceso: usa este servicio y el de catálogo.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CuentasApplication {

  public static void main(String[] args) {
    SpringApplication.run(CuentasApplication.class, args);
  }
}
