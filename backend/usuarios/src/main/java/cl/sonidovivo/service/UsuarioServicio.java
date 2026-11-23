package cl.sonidovivo.service;

import cl.sonidovivo.dto.PerfilInterno;
import cl.sonidovivo.dto.RegistroSolicitud;
import cl.sonidovivo.dto.UsuarioRespuesta;
import cl.sonidovivo.dto.UsuarioSolicitud;
import cl.sonidovivo.modelo.Rol;
import cl.sonidovivo.modelo.Usuario;
import cl.sonidovivo.repository.UsuarioRepositorio;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioServicio {

  private final UsuarioRepositorio usuarios;
  private final PasswordEncoder encoder;
  private final ClienteAuth auth;

  public UsuarioServicio(UsuarioRepositorio usuarios, PasswordEncoder encoder, ClienteAuth auth) {
    this.usuarios = usuarios;
    this.encoder = encoder;
    this.auth = auth;
  }

  public List<UsuarioRespuesta> listar() {
    return usuarios.findAll().stream().map(UsuarioRespuesta::desde).toList();
  }

  public PerfilInterno validar(String correo, String clave) {
    Usuario usuario = usuarios.findByCorreoIgnoreCase(correo.trim())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
    if (!encoder.matches(clave, usuario.getClave())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
    }
    return perfil(usuario);
  }

  @Transactional
  public PerfilInterno registrar(RegistroSolicitud datos) {
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
    return perfil(usuarios.save(usuario));
  }

  public PerfilInterno porId(Long id) {
    return perfil(obtenerEntidad(id));
  }

  public PerfilInterno porCorreo(String correo) {
    return usuarios.findByCorreoIgnoreCase(correo)
        .map(this::perfil)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión."));
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
    auth.revocar(usuario.getId());
    usuarios.delete(usuario);
  }

  private Usuario obtenerEntidad(Long id) {
    return usuarios.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.")
    );
  }

  private PerfilInterno perfil(Usuario usuario) {
    return new PerfilInterno(
        usuario.getId(),
        usuario.getRun(),
        usuario.getNombre(),
        usuario.getApellidos(),
        usuario.getCorreo(),
        usuario.getRol().getEtiqueta(),
        usuario.getRol().name(),
        usuario.getRegion(),
        usuario.getComuna(),
        usuario.getDireccion(),
        usuario.getFechaNacimiento()
    );
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
