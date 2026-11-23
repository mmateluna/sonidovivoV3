package cl.sonidovivo.repository;

import cl.sonidovivo.modelo.Producto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data implementa el CRUD. Este es el patrón Repositorio del backend.
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
  Optional<Producto> findByCodigo(String codigo);
}
