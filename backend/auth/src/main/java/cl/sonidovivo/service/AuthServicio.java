package cl.sonidovivo.service;

import cl.sonidovivo.dto.PerfilInterno;
import cl.sonidovivo.dto.RegistroSolicitud;
import cl.sonidovivo.dto.SesionRespuesta;
import cl.sonidovivo.dto.UsuarioRespuesta;
import cl.sonidovivo.modelo.TokenRefresco;
import cl.sonidovivo.repository.TokenRefrescoRepositorio;
import cl.sonidovivo.seguridad.JwtServicio;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServicio {

  private final TokenRefrescoRepositorio tokens;
  private final JwtServicio jwt;
  private final ClienteUsuarios usuarios;

  public AuthServicio(TokenRefrescoRepositorio tokens, JwtServicio jwt, ClienteUsuarios usuarios) {
    this.tokens = tokens;
    this.jwt = jwt;
    this.usuarios = usuarios;
  }

  @Transactional
  public SesionRespuesta login(String correo, String clave) {
    PerfilInterno perfil = usuarios.validar(correo, clave);
    tokens.deleteByUsuarioId(perfil.id());
    return emitir(perfil);
  }

  @Transactional
  public SesionRespuesta registrar(RegistroSolicitud datos) {
    return emitir(usuarios.registrar(datos));
  }

  public UsuarioRespuesta yo(String correo) {
    return usuarios.porCorreo(correo).publico();
  }

  @Transactional
  public SesionRespuesta renovar(String refresh) {
    Claims claims;
    try {
      claims = jwt.leer(refresh);
    } catch (RuntimeException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión expiró. Vuelve a ingresar.");
    }
    if (!"refresh".equals(claims.get("tipo", String.class))) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El token no sirve para renovar la sesión.");
    }

    TokenRefresco guardado = tokens.findByValor(refresh).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión ya no es válida. Vuelve a ingresar.")
    );
    PerfilInterno perfil = usuarios.porId(guardado.getUsuarioId());
    // Rotación: el refresh usado se borra y se entrega otro.
    tokens.delete(guardado);
    return emitir(perfil);
  }

  @Transactional
  public void salir(String correo) {
    tokens.deleteByCorreo(correo);
  }

  @Transactional
  public void revocar(long usuarioId) {
    tokens.deleteByUsuarioId(usuarioId);
  }

  private SesionRespuesta emitir(PerfilInterno perfil) {
    String acceso = jwt.crearAcceso(perfil);
    String refresh = jwt.crearRefresh(perfil);

    TokenRefresco guardado = new TokenRefresco();
    guardado.setValor(refresh);
    guardado.setUsuarioId(perfil.id());
    guardado.setCorreo(perfil.correo());
    guardado.setExpira(jwt.vencimientoRefresh());
    tokens.save(guardado);

    return new SesionRespuesta(acceso, refresh, perfil.publico());
  }
}
