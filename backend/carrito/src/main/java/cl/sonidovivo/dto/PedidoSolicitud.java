package cl.sonidovivo.dto;

import jakarta.validation.constraints.NotBlank;

public record PedidoSolicitud(@NotBlank String metodo) {}
