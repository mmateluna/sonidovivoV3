package cl.sonidovivo.repository;

import cl.sonidovivo.modelo.CarritoItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoItemRepositorio extends JpaRepository<CarritoItem, Long> {

  List<CarritoItem> findByUsuarioId(long usuarioId);

  Optional<CarritoItem> findByUsuarioIdAndProductoId(long usuarioId, long productoId);

  void deleteByUsuarioId(long usuarioId);
}
