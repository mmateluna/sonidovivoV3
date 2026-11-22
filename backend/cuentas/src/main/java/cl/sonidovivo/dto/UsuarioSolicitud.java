package cl.sonidovivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Alta y edición desde el panel. La clave puede ir vacía al editar.
public record UsuarioSolicitud(
    @NotBlank(message = "El RUN es obligatorio.") String run,
    @NotBlank(message = "El nombre es obligatorio.") @Size(max = 50) String nombre,
    @NotBlank(message = "Los apellidos son obligatorios.") @Size(max = 100) String apellidos,
    @NotBlank(message = "El correo es obligatorio.") String correo,
    String clave,
    @NotBlank(message = "Selecciona un tipo de usuario.") String tipo,
    String region,
    String comuna,
    @Size(max = 300) String direccion,
    String fechaNacimiento
) {}
