package cl.sonidovivo.repositorio;

import cl.sonidovivo.modelo.TokenRefresco;
import cl.sonidovivo.modelo.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRefrescoRepositorio extends JpaRepository<TokenRefresco, Long> {
  Optional<TokenRefresco> findByValor(String valor);

  void deleteByUsuario(Usuario usuario);
}
