package cl.sonidovivo.dto;

import jakarta.validation.constraints.NotBlank;

public record PagoSolicitud(@NotBlank(message = "Elige un medio de pago.") String metodo) {}
