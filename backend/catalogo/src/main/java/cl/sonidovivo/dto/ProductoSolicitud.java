package cl.sonidovivo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoSolicitud(
    @NotBlank(message = "El código del producto es obligatorio.")
    @Size(min = 3, max = 20, message = "El código debe tener al menos 3 caracteres.")
    String codigo,
    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    String nombre,
    @Size(max = 80) String marca,
    @Size(max = 80) String modelo,
    @NotNull(message = "El precio es obligatorio.")
    @Min(value = 0, message = "El precio no puede ser negativo.")
    Integer precio,
    @NotBlank(message = "Selecciona una categoría.")
    @Size(max = 80) String categoria,
    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 0, message = "El stock debe ser un número entero igual o mayor a 0.")
    Integer stock,
    @Min(value = 0, message = "El stock crítico debe ser un número entero igual o mayor a 0.")
    Integer stockCritico,
    @Size(max = 300) String imagen,
    @Size(max = 500, message = "La descripción no puede superar 500 caracteres.")
    String descripcion
) {}
