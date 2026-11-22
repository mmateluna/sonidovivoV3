import { createContext, useContext } from "react";
import type { UsuarioAdmin } from "../tipos";
import type { DatosUsuario } from "../utilidades/validaciones";

export interface AuthContextValue {
  usuario: UsuarioAdmin | null;
  listo: boolean;
  iniciarSesion: (correo: string, clave: string) => Promise<string | void>;
  registrarCuenta: (datos: DatosUsuario) => Promise<string | void>;
  cerrarSesion: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

export function useAuth(): AuthContextValue {
  const contexto = useContext(AuthContext);
  if (!contexto) {
    throw new Error("useAuth debe usarse dentro de AuthProvider");
  }
  return contexto;
}
