package cl.sonidovivo.repositorio;

import cl.sonidovivo.modelo.Rol;
import cl.sonidovivo.modelo.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByCorreoIgnoreCase(String correo);

  Optional<Usuario> findByRun(String run);

  long countByRol(Rol rol);
}
