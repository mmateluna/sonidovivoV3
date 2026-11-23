package cl.sonidovivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MensajeSolicitud(
    @NotBlank(message = "El nombre es obligatorio.") @Size(max = 100) String nombre,
    @NotBlank(message = "El correo es obligatorio.") @Size(max = 100) String correo,
    @NotBlank(message = "El comentario es obligatorio.") @Size(max = 500) String comentario
) {}
