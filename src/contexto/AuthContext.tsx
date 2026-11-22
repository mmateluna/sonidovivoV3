import { useEffect, useState, type ReactNode } from "react";
import {
  alPerderSesion,
  alRenovar,
  credencialesActuales,
  login,
  registrar,
  salir,
  usarCredenciales,
  yo,
} from "../servicios/api";
import type { Sesion, UsuarioAdmin } from "../tipos";
import type { DatosUsuario } from "../utilidades/validaciones";
import { AuthContext } from "./authCompartido";

export { useAuth } from "./authCompartido";

// La sesión se guarda aquí para sobrevivir a un reload.
// No guardamos la contraseña: solo los dos tokens y los datos públicos del usuario.
const CLAVE_SESION = "sesionSonidoVivo";

function leerGuardada(): Sesion | null {
  const texto = localStorage.getItem(CLAVE_SESION);
  if (!texto) return null;
  try {
    const sesion = JSON.parse(texto) as Sesion;
    if (!sesion.token || !sesion.refresh || !sesion.usuario) return null;
    return sesion;
  } catch {
    return null;
  }
}

function guardarEnDisco(sesion: Sesion | null) {
  if (sesion) localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
  else localStorage.removeItem(CLAVE_SESION);
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioAdmin | null>(null);
  const [listo, setListo] = useState(false);

  const aplicar = (sesion: Sesion | null) => {
    usarCredenciales(sesion?.token ?? null, sesion?.refresh ?? null);
    guardarEnDisco(sesion);
    setUsuario(sesion?.usuario ?? null);
  };

  useEffect(() => {
    alRenovar((tokenNuevo, refreshNuevo) => {
      const guardada = leerGuardada();
      if (!guardada) return;
      aplicar({ ...guardada, token: tokenNuevo, refresh: refreshNuevo });
    });
    alPerderSesion(() => aplicar(null));

    const guardada = leerGuardada();
    if (!guardada) {
      setListo(true);
      return;
    }
    usarCredenciales(guardada.token, guardada.refresh);
    yo()
      .then((actual) => {
        const credenciales = credencialesActuales();
        aplicar({
          token: credenciales.token ?? guardada.token,
          refresh: credenciales.refresh ?? guardada.refresh,
          usuario: actual,
        });
      })
      .catch(() => aplicar(null))
      .finally(() => setListo(true));
  }, []);

  const iniciarSesion = async (correo: string, clave: string): Promise<string | void> => {
    try {
      aplicar(await login(correo, clave));
    } catch (error) {
      return error instanceof Error ? error.message : "No se pudo iniciar sesión.";
    }
  };

  const registrarCuenta = async (datos: DatosUsuario): Promise<string | void> => {
    try {
      aplicar(await registrar(datos));
    } catch (error) {
      return error instanceof Error ? error.message : "No se pudo registrar.";
    }
  };

  const cerrarSesion = async () => {
    try {
      await salir();
    } catch {
      // Si el token ya no sirve, igual borramos la sesión de este navegador.
    }
    aplicar(null);
  };

  return (
    <AuthContext.Provider value={{ usuario, listo, iniciarSesion, registrarCuenta, cerrarSesion }}>
      {children}
    </AuthContext.Provider>
  );
}
