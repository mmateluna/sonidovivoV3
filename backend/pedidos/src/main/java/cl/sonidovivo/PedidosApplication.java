package cl.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Pedidos confirmados. El carrito y el medio de pago viven en otros servicios.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class PedidosApplication {

  public static void main(String[] args) {
    SpringApplication.run(PedidosApplication.class, args);
  }
}
