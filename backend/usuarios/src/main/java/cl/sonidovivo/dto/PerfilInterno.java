package cl.sonidovivo.dto;

import cl.sonidovivo.modelo.Usuario;

public record PerfilInterno(
    Long id,
    String run,
    String nombre,
    String apellidos,
    String correo,
    String tipo,
    String rol,
    String region,
    String comuna,
    String direccion,
    String fechaNacimiento
) {
  public static PerfilInterno desde(Usuario usuario) {
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
}
