package cl.sonidovivo.dto;

import jakarta.validation.constraints.NotBlank;

// El registro público siempre crea un Cliente. El rol no se pide aquí.
public record RegistroSolicitud(
    @NotBlank(message = "El RUN es obligatorio.") String run,
    @NotBlank(message = "El nombre es obligatorio.") String nombre,
    @NotBlank(message = "Los apellidos son obligatorios.") String apellidos,
    @NotBlank(message = "El correo es obligatorio.") String correo,
    @NotBlank(message = "La contraseña es obligatoria.") String clave,
    String region,
    String comuna,
    String direccion,
    String fechaNacimiento
) {}
