package cl.sonidovivo.repositorio;

import cl.sonidovivo.modelo.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {

  List<Pedido> findByUsuarioIdOrderByIdDesc(long usuarioId);
}
