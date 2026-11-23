package cl.sonidovivo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LineaPedido(
    @Min(1) long productoId,
    @NotBlank @Size(max = 100) String nombre,
    @Min(0) int precio,
    @Min(1) int cantidad
) {}
