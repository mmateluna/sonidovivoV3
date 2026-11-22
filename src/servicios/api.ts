/* ============================================
   SONIDO VIVO - api.ts
   El navegador no habla con la base de datos.
   Pide a este archivo, y este archivo pide a Spring Boot.

   El token de acceso viaja en el header Authorization.
   Si Spring responde 401, se usa el refresh token una vez
   para pedir una sesión nueva y se reintenta la llamada.
   ============================================ */

import type { ItemCarrito, Producto, Sesion, UsuarioAdmin } from "../tipos";
import type { DatosUsuario } from "../utilidades/validaciones";

let token: string | null = null;
let refresh: string | null = null;
let alActualizar: ((token: string, refresh: string) => void) | null = null;
let alPerder: (() => void) | null = null;
let renovacionEnCurso: Promise<boolean> | null = null;

export function usarCredenciales(nuevoToken: string | null, nuevoRefresh: string | null) {
  token = nuevoToken;
  refresh = nuevoRefresh;
}

export function credencialesActuales() {
  return { token, refresh };
}

export function alRenovar(fn: (token: string, refresh: string) => void) {
  alActualizar = fn;
}

export function alPerderSesion(fn: () => void) {
  alPerder = fn;
}

async function mensajeDe(respuesta: Response): Promise<string> {
  try {
    const cuerpo = (await respuesta.json()) as { mensaje?: string };
    if (cuerpo.mensaje) return cuerpo.mensaje;
  } catch {
    // La respuesta no traía JSON.
  }
  return "No se pudo completar la operación.";
}

async function renovarSiSePuede(): Promise<boolean> {
  if (!refresh) return false;
  if (!renovacionEnCurso) {
    const refreshActual = refresh;
    renovacionEnCurso = (async () => {
      try {
        const respuesta = await fetch("/api/auth/renovar", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ refresh: refreshActual }),
        });
        if (!respuesta.ok) {
          alPerder?.();
          return false;
        }
        const datos = (await respuesta.json()) as { token: string; refresh: string };
        token = datos.token;
        refresh = datos.refresh;
        alActualizar?.(datos.token, datos.refresh);
        return true;
      } catch {
        alPerder?.();
        return false;
      } finally {
        renovacionEnCurso = null;
      }
    })();
  }
  return renovacionEnCurso;
}

async function pedir<T>(ruta: string, opciones: RequestInit = {}, conAuth = true): Promise<T> {
  const ejecutar = () => {
    const headers = new Headers(opciones.headers);
    if (opciones.body) headers.set("Content-Type", "application/json");
    if (conAuth && token) headers.set("Authorization", `Bearer ${token}`);
    return fetch(ruta, { ...opciones, headers });
  };

  let respuesta = await ejecutar();
  if (respuesta.status === 401 && conAuth && (await renovarSiSePuede())) {
    respuesta = await ejecutar();
  }
  if (!respuesta.ok) throw new Error(await mensajeDe(respuesta));
  if (respuesta.status === 204) return undefined as T;
  return respuesta.json() as Promise<T>;
}

function pedirJson<T>(ruta: string, metodo: string, cuerpo?: unknown, conAuth = true): Promise<T> {
  return pedir<T>(
    ruta,
    { method: metodo, body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo) },
    conAuth,
  );
}

export function listarProductos(): Promise<Producto[]> {
  return pedir<Producto[]>("/api/productos", {}, false);
}

export function obtenerProducto(id: number): Promise<Producto | null> {
  return pedir<Producto>(`/api/productos/${id}`, {}, false).catch((error: Error) => {
    if (error.message === "Producto no encontrado.") return null;
    throw error;
  });
}

export function crearProducto(datos: Omit<Producto, "id">): Promise<Producto> {
  return pedirJson<Producto>("/api/productos", "POST", datos);
}

export function actualizarProducto(id: number, datos: Omit<Producto, "id">): Promise<Producto> {
  return pedirJson<Producto>(`/api/productos/${id}`, "PUT", datos);
}

export function eliminarProducto(id: number): Promise<void> {
  return pedir<void>(`/api/productos/${id}`, { method: "DELETE" });
}

export function listarUsuarios(): Promise<UsuarioAdmin[]> {
  return pedir<UsuarioAdmin[]>("/api/usuarios");
}

export function crearUsuario(datos: DatosUsuario): Promise<UsuarioAdmin> {
  return pedirJson<UsuarioAdmin>("/api/usuarios", "POST", cuerpoUsuario(datos));
}

export function actualizarUsuario(id: number, datos: DatosUsuario): Promise<UsuarioAdmin> {
  return pedirJson<UsuarioAdmin>(`/api/usuarios/${id}`, "PUT", cuerpoUsuario(datos));
}

export function eliminarUsuario(id: number): Promise<void> {
  return pedir<void>(`/api/usuarios/${id}`, { method: "DELETE" });
}

export function login(correo: string, clave: string): Promise<Sesion> {
  return pedirJson<Sesion>("/api/auth/login", "POST", { correo, clave }, false);
}

export function registrar(datos: DatosUsuario): Promise<Sesion> {
  return pedirJson<Sesion>(
    "/api/auth/registro",
    "POST",
    {
      run: datos.run.trim(),
      nombre: datos.nombre.trim(),
      apellidos: datos.apellidos.trim(),
      correo: datos.correo.trim(),
      clave: datos.clave ?? "",
      region: datos.region,
      comuna: datos.comuna,
      direccion: datos.direccion.trim(),
      fechaNacimiento: datos.fechaNacimiento,
    },
    false,
  );
}

export function yo(): Promise<UsuarioAdmin> {
  return pedir<UsuarioAdmin>("/api/auth/yo");
}

export function salir(): Promise<void> {
  return pedir<void>("/api/auth/salir", { method: "POST" });
}

export function listarCarrito(): Promise<ItemCarrito[]> {
  return pedir<ItemCarrito[]>("/api/carrito");
}

export function guardarItemCarrito(item: ItemCarrito): Promise<ItemCarrito> {
  return pedirJson<ItemCarrito>("/api/carrito", "PUT", item);
}

export function eliminarItemCarrito(id: number): Promise<void> {
  return pedir<void>(`/api/carrito/items/${id}`, { method: "DELETE" });
}

export function vaciarCarrito(): Promise<void> {
  return pedir<void>("/api/carrito", { method: "DELETE" });
}

export function crearPedido(metodo: string): Promise<{ id: number; metodo: string; total: number }> {
  return pedirJson("/api/pedidos", "POST", { metodo });
}

function cuerpoUsuario(datos: DatosUsuario) {
  return {
    run: datos.run.trim(),
    nombre: datos.nombre.trim(),
    apellidos: datos.apellidos.trim(),
    correo: datos.correo.trim(),
    clave: datos.clave ?? "",
    tipo: datos.tipo,
    region: datos.region,
    comuna: datos.comuna,
    direccion: datos.direccion.trim(),
    fechaNacimiento: datos.fechaNacimiento,
  };
}
