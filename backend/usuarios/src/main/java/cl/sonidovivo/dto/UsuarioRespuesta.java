package cl.sonidovivo.dto;

import cl.sonidovivo.modelo.Usuario;

// Lo que ve el frontend. La contraseña no viaja.
public record UsuarioRespuesta(
    Long id,
    String run,
    String nombre,
    String apellidos,
    String correo,
    String tipo,
    String region,
    String comuna,
    String direccion,
    String fechaNacimiento
) {
  public static UsuarioRespuesta desde(Usuario usuario) {
    return new UsuarioRespuesta(
        usuario.getId(),
        usuario.getRun(),
        usuario.getNombre(),
        usuario.getApellidos(),
        usuario.getCorreo(),
        usuario.getRol().getEtiqueta(),
        usuario.getRegion(),
        usuario.getComuna(),
        usuario.getDireccion(),
        usuario.getFechaNacimiento()
    );
  }
}
