package cl.sonidovivo.dto;

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
) {}
