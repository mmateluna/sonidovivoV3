package cl.sonidovivo.dto;

import jakarta.validation.constraints.NotBlank;

public record RenovarSolicitud(
    @NotBlank(message = "Falta el token de renovación.") String refresh
) {}
