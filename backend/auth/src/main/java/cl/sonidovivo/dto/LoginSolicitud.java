package cl.sonidovivo.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginSolicitud(
    @NotBlank(message = "El correo es obligatorio.") String correo,
    @NotBlank(message = "La contraseña es obligatoria.") String clave
) {}
