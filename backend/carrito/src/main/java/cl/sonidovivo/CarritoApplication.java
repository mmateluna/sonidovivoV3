package cl.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Microservicio de carrito y pedidos. El usuario llega en el JWT (claim uid).
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CarritoApplication {

  public static void main(String[] args) {
    SpringApplication.run(CarritoApplication.class, args);
  }
}
