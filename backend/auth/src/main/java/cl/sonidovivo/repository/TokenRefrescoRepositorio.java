package cl.sonidovivo.repository;

import cl.sonidovivo.modelo.TokenRefresco;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRefrescoRepositorio extends JpaRepository<TokenRefresco, Long> {
  Optional<TokenRefresco> findByValor(String valor);

  void deleteByCorreo(String correo);

  void deleteByUsuarioId(long usuarioId);
}
