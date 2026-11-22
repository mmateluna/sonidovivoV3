package cl.sonidovivo.negocio;

import cl.sonidovivo.dto.UsuarioRespuesta;
import cl.sonidovivo.dto.UsuarioSolicitud;
import cl.sonidovivo.modelo.Rol;
import cl.sonidovivo.modelo.Usuario;
import cl.sonidovivo.repositorio.TokenRefrescoRepositorio;
import cl.sonidovivo.repositorio.UsuarioRepositorio;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioServicio {

  private final UsuarioRepositorio usuarios;
  private final TokenRefrescoRepositorio tokens;
  private final PasswordEncoder encoder;

  public UsuarioServicio(
      UsuarioRepositorio usuarios,
      TokenRefrescoRepositorio tokens,
      PasswordEncoder encoder
  ) {
    this.usuarios = usuarios;
    this.tokens = tokens;
    this.encoder = encoder;
  }

  public List<UsuarioRespuesta> listar() {
    return usuarios.findAll().stream().map(UsuarioRespuesta::desde).toList();
  }

  public Usuario obtenerEntidad(Long id) {
    return usuarios.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.")
    );
  }

  @Transactional
  public UsuarioRespuesta crear(UsuarioSolicitud datos) {
    ValidadorDatos.clave(datos.clave());
    Usuario usuario = new Usuario();
    aplicar(usuario, datos, true);
    usuario.setClave(encoder.encode(datos.clave()));
    return UsuarioRespuesta.desde(usuarios.save(usuario));
  }

  @Transactional
  public UsuarioRespuesta actualizar(Long id, UsuarioSolicitud datos) {
    Usuario usuario = obtenerEntidad(id);
    Rol rolNuevo = rolDe(datos.tipo());
    if (usuario.getRol() == Rol.ADMINISTRADOR && rolNuevo != Rol.ADMINISTRADOR && esElUltimoAdmin()) {
      throw new ReglaNegocioException("Debe quedar al menos un administrador.");
    }
    aplicar(usuario, datos, false);
    if (datos.clave() != null && !datos.clave().isBlank()) {
      ValidadorDatos.clave(datos.clave());
      usuario.setClave(encoder.encode(datos.clave()));
    }
    return UsuarioRespuesta.desde(usuarios.save(usuario));
  }

  @Transactional
  public void eliminar(Long id, String correoAutenticado) {
    Usuario usuario = obtenerEntidad(id);
    if (usuario.getCorreo().equalsIgnoreCase(correoAutenticado)) {
      throw new ReglaNegocioException("No puedes eliminar tu propia cuenta.");
    }
    if (usuario.getRol() == Rol.ADMINISTRADOR && esElUltimoAdmin()) {
      throw new ReglaNegocioException("Debe quedar al menos un administrador.");
    }
    tokens.deleteByUsuario(usuario);
    usuarios.delete(usuario);
  }

  private void aplicar(Usuario usuario, UsuarioSolicitud datos, boolean esNuevo) {
    String run = ValidadorDatos.run(datos.run());
    String correo = ValidadorDatos.correo(datos.correo());
    if (esNuevo || !run.equals(usuario.getRun())) {
      if (usuarios.findByRun(run).isPresent()) {
        throw new ReglaNegocioException("Este RUN ya está registrado.");
      }
    }
    usuarios.findByCorreoIgnoreCase(correo)
        .filter(otro -> usuario.getId() == null || !otro.getId().equals(usuario.getId()))
        .ifPresent(otro -> {
          throw new ReglaNegocioException("Este correo ya está registrado.");
        });

    usuario.setRun(run);
    usuario.setNombre(ValidadorDatos.obligatorio(datos.nombre(), "El nombre es obligatorio."));
    usuario.setApellidos(ValidadorDatos.obligatorio(datos.apellidos(), "Los apellidos son obligatorios."));
    usuario.setCorreo(correo);
    usuario.setRol(rolDe(datos.tipo()));
    usuario.setRegion(texto(datos.region()));
    usuario.setComuna(texto(datos.comuna()));
    usuario.setDireccion(ValidadorDatos.obligatorio(datos.direccion(), "La dirección es obligatoria."));
    usuario.setFechaNacimiento(texto(datos.fechaNacimiento()));
  }

  private Rol rolDe(String tipo) {
    return Rol.buscar(tipo).orElseThrow(() -> new ReglaNegocioException("El tipo de usuario no es válido."));
  }

  private boolean esElUltimoAdmin() {
    return usuarios.countByRol(Rol.ADMINISTRADOR) <= 1;
  }

  private String texto(String valor) {
    return valor == null ? "" : valor.trim();
  }
}
