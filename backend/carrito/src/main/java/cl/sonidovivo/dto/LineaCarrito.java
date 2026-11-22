package cl.sonidovivo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// id es el del producto en el catálogo, no el de la fila del carrito.
public record LineaCarrito(
    @NotNull @Min(1) Long id,
    @NotBlank @Size(max = 100) String nombre,
    @Min(0) int precio,
    @Size(max = 300) String imagen,
    @Min(1) int cantidad,
    @Min(0) int stock
) {}
