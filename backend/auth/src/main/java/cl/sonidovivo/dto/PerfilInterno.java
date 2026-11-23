package cl.sonidovivo.dto;

// Lo que devuelve el servicio de usuarios cuando auth pide validar una cuenta.
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
  public UsuarioRespuesta publico() {
    return new UsuarioRespuesta(
        id, run, nombre, apellidos, correo, tipo, region, comuna, direccion, fechaNacimiento
    );
  }
}
