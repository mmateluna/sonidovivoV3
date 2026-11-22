package cl.sonidovivo.negocio;

import cl.sonidovivo.dto.RegistroSolicitud;
import cl.sonidovivo.dto.SesionRespuesta;
import cl.sonidovivo.dto.UsuarioRespuesta;
import cl.sonidovivo.modelo.Rol;
import cl.sonidovivo.modelo.TokenRefresco;
import cl.sonidovivo.modelo.Usuario;
import cl.sonidovivo.repositorio.TokenRefrescoRepositorio;
import cl.sonidovivo.repositorio.UsuarioRepositorio;
import cl.sonidovivo.seguridad.JwtServicio;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServicio {

  private final UsuarioRepositorio usuarios;
  private final TokenRefrescoRepositorio tokens;
  private final PasswordEncoder encoder;
  private final JwtServicio jwt;

  public AuthServicio(
      UsuarioRepositorio usuarios,
      TokenRefrescoRepositorio tokens,
      PasswordEncoder encoder,
      JwtServicio jwt
  ) {
    this.usuarios = usuarios;
    this.tokens = tokens;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  @Transactional
  public SesionRespuesta login(String correo, String clave) {
    Usuario usuario = usuarios.findByCorreoIgnoreCase(correo.trim())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
    if (!encoder.matches(clave, usuario.getClave())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
    }
    tokens.deleteByUsuario(usuario);
    return emitir(usuario);
  }

  @Transactional
  public SesionRespuesta registrar(RegistroSolicitud datos) {
    ValidadorDatos.clave(datos.clave());
    String run = ValidadorDatos.run(datos.run());
    String correo = ValidadorDatos.correo(datos.correo());
    if (usuarios.findByRun(run).isPresent()) {
      throw new ReglaNegocioException("Este RUN ya está registrado.");
    }
    if (usuarios.findByCorreoIgnoreCase(correo).isPresent()) {
      throw new ReglaNegocioException("Este correo ya está registrado.");
    }

    Usuario usuario = new Usuario();
    usuario.setRun(run);
    usuario.setNombre(ValidadorDatos.obligatorio(datos.nombre(), "El nombre es obligatorio."));
    usuario.setApellidos(ValidadorDatos.obligatorio(datos.apellidos(), "Los apellidos son obligatorios."));
    usuario.setCorreo(correo);
    usuario.setClave(encoder.encode(datos.clave()));
    usuario.setRol(Rol.CLIENTE);
    usuario.setRegion(texto(datos.region()));
    usuario.setComuna(texto(datos.comuna()));
    usuario.setDireccion(ValidadorDatos.obligatorio(datos.direccion(), "La dirección es obligatoria."));
    usuario.setFechaNacimiento(texto(datos.fechaNacimiento()));
    return emitir(usuarios.save(usuario));
  }

  public UsuarioRespuesta yo(String correo) {
    Usuario usuario = usuarios.findByCorreoIgnoreCase(correo).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión.")
    );
    return UsuarioRespuesta.desde(usuario);
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
    Usuario usuario = guardado.getUsuario();
    // Rotación: el refresh usado se borra y se entrega otro.
    tokens.delete(guardado);
    return emitir(usuario);
  }

  @Transactional
  public void salir(String correo) {
    usuarios.findByCorreoIgnoreCase(correo).ifPresent(tokens::deleteByUsuario);
  }

  private SesionRespuesta emitir(Usuario usuario) {
    String acceso = jwt.crearAcceso(usuario);
    String refresh = jwt.crearRefresh(usuario);

    TokenRefresco guardado = new TokenRefresco();
    guardado.setValor(refresh);
    guardado.setUsuario(usuario);
    guardado.setExpira(jwt.vencimientoRefresh());
    tokens.save(guardado);

    return new SesionRespuesta(acceso, refresh, UsuarioRespuesta.desde(usuario));
  }

  private String texto(String valor) {
    return valor == null ? "" : valor.trim();
  }
}
