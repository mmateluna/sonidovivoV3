package cl.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Registra el medio de pago simulado y pide al carrito y a pedidos que cierren la compra.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class PagosApplication {

  public static void main(String[] args) {
    SpringApplication.run(PagosApplication.class, args);
  }
}
