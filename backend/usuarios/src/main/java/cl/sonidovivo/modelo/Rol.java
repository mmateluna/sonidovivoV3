package cl.sonidovivo.modelo;

import java.util.Optional;

// Los tres roles del caso. La etiqueta es el texto que ya usa el frontend.
public enum Rol {
  ADMINISTRADOR("Administrador"),
  VENDEDOR("Vendedor"),
  CLIENTE("Cliente");

  private final String etiqueta;

  Rol(String etiqueta) {
    this.etiqueta = etiqueta;
  }

  public String getEtiqueta() {
    return etiqueta;
  }

  public static Optional<Rol> buscar(String texto) {
    if (texto == null) {
      return Optional.empty();
    }
    String limpio = texto.trim();
    for (Rol rol : values()) {
      if (rol.name().equalsIgnoreCase(limpio) || rol.etiqueta.equalsIgnoreCase(limpio)) {
        return Optional.of(rol);
      }
    }
    return Optional.empty();
  }
}
