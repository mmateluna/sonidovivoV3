package cl.sonidovivo.negocio;

import java.util.regex.Pattern;

// Las mismas reglas que validaciones.ts, ahora del lado del servidor.
public final class ValidadorDatos {

  private static final Pattern CORREO = Pattern.compile("(?i)^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$");

  private ValidadorDatos() {}

  public static String obligatorio(String valor, String mensaje) {
    if (valor == null || valor.trim().isEmpty()) {
      throw new ReglaNegocioException(mensaje);
    }
    return valor.trim();
  }

  public static String correo(String correo) {
    String limpio = obligatorio(correo, "El correo es obligatorio.").toLowerCase();
    if (!CORREO.matcher(limpio).matches()) {
      throw new ReglaNegocioException("Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com.");
    }
    return limpio;
  }

  public static void clave(String clave) {
    if (clave == null || clave.length() < 4 || clave.length() > 10) {
      throw new ReglaNegocioException("La contraseña debe tener entre 4 y 10 caracteres.");
    }
  }

  public static String run(String run) {
    String limpio = obligatorio(run, "El RUN es obligatorio.").toUpperCase();
    if (limpio.contains(".") || limpio.contains("-")) {
      throw new ReglaNegocioException("Ingresa el RUN sin puntos ni guión (ej: 123456785).");
    }
    if (!runValido(limpio)) {
      throw new ReglaNegocioException("El RUN no es válido. Revisa el dígito verificador.");
    }
    return limpio;
  }

  private static boolean runValido(String run) {
    if (!run.matches("^[0-9]+[0-9K]$")) {
      return false;
    }
    String cuerpo = run.substring(0, run.length() - 1);
    String dv = run.substring(run.length() - 1);
    return dv.equals(digito(cuerpo));
  }

  private static String digito(String cuerpo) {
    int suma = 0;
    int multiplicador = 2;
    for (int i = cuerpo.length() - 1; i >= 0; i--) {
      suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplicador;
      multiplicador++;
      if (multiplicador > 7) {
        multiplicador = 2;
      }
    }
    int resultado = 11 - (suma % 11);
    if (resultado == 11) {
      return "0";
    }
    if (resultado == 10) {
      return "K";
    }
    return String.valueOf(resultado);
  }
}
