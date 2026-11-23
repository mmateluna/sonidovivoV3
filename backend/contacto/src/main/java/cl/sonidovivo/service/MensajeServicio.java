package cl.sonidovivo.service;

import cl.sonidovivo.dto.MensajeSolicitud;
import cl.sonidovivo.modelo.Mensaje;
import cl.sonidovivo.repository.MensajeRepositorio;
import java.time.Instant;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MensajeServicio {

  private static final Pattern CORREO = Pattern.compile(
      "^[a-z0-9._%+-]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$"
  );

  private final MensajeRepositorio mensajes;

  public MensajeServicio(MensajeRepositorio mensajes) {
    this.mensajes = mensajes;
  }

  @Transactional
  public Mensaje guardar(MensajeSolicitud datos) {
    String correo = datos.correo().trim().toLowerCase();
    if (!CORREO.matcher(correo).matches()) {
      throw new ReglaNegocioException("Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com.");
    }
    Mensaje mensaje = new Mensaje();
    mensaje.setNombre(datos.nombre().trim());
    mensaje.setCorreo(correo);
    mensaje.setComentario(datos.comentario().trim());
    mensaje.setCreado(Instant.now());
    return mensajes.save(mensaje);
  }
}
