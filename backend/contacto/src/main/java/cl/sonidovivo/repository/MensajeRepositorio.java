package cl.sonidovivo.repository;

import cl.sonidovivo.modelo.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepositorio extends JpaRepository<Mensaje, Long> {}
