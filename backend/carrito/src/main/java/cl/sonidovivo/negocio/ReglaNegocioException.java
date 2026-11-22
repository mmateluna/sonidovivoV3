package cl.sonidovivo.negocio;

// Error de una regla del negocio (RUN repetido, código duplicado, último admin...).
public class ReglaNegocioException extends RuntimeException {

  public ReglaNegocioException(String mensaje) {
    super(mensaje);
  }
}
