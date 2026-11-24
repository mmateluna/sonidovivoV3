import { createContext, useContext, useEffect, useRef, useState, type ReactNode } from "react";
import type { ItemCarrito, Producto } from "../tipos";
import {
  eliminarItemCarrito,
  guardarItemCarrito,
  listarCarrito,
  vaciarCarrito,
} from "../servicios/api";
import { useAuth } from "./AuthContext";

// El invitado sigue en el navegador. Con sesión, el carrito vive en su microservicio.
export const CLAVE_CARRITO = "carritoSonidoVivo";

interface CarritoContextoTipo {
  items: ItemCarrito[];
  totalItems: number;
  total: number;
  agregar: (producto: Producto, cantidad?: number) => void;
  cambiarCantidad: (id: number, cantidad: number) => void;
  eliminar: (id: number) => void;
  vaciar: () => void;
}

const CarritoContexto = createContext<CarritoContextoTipo | undefined>(undefined);

// Una sola sincronización por usuario, aunque React monte el efecto dos veces.
const sincronizaciones = new Map<number, Promise<ItemCarrito[]>>();

function leerCarritoGuardado(): ItemCarrito[] {
  try {
    const datos = localStorage.getItem(CLAVE_CARRITO);
    return datos
      ? (JSON.parse(datos) as ItemCarrito[]).map((item) => ({ ...item, stock: item.stock ?? 99 }))
      : [];
  } catch {
    return [];
  }
}

async function sincronizar(usuarioId: number): Promise<ItemCarrito[]> {
  const previa = sincronizaciones.get(usuarioId);
  if (previa) return previa;
  const trabajo = (async () => {
    const local = leerCarritoGuardado();
    let remoto = await listarCarrito();
    if (local.length > 0) {
      for (const item of local) {
        const ya = remoto.find((linea) => linea.id === item.id);
        const tope = item.stock > 0 ? item.stock : 99;
        const cantidad = Math.min((ya?.cantidad ?? 0) + item.cantidad, tope);
        await guardarItemCarrito({ ...item, cantidad });
      }
      localStorage.removeItem(CLAVE_CARRITO);
      remoto = await listarCarrito();
    }
    return remoto;
  })();
  sincronizaciones.set(usuarioId, trabajo);
  return trabajo;
}

export function CarritoProvider({ children }: { children: ReactNode }) {
  const { usuario, listo } = useAuth();
  const [items, setItems] = useState<ItemCarrito[]>(leerCarritoGuardado);
  const habiaSesion = useRef(false);
  const omitirGuardado = useRef(false);

  useEffect(() => {
    if (!listo) return;
    if (usuario?.id) {
      habiaSesion.current = true;
      let activo = true;
      sincronizar(usuario.id)
        .then((remotos) => {
          if (activo) setItems(remotos);
        })
        .catch(() => {
          // Si el servicio de carrito no responde, se sigue viendo lo local.
        });
      return () => {
        activo = false;
      };
    }
    if (habiaSesion.current) {
      habiaSesion.current = false;
      sincronizaciones.clear();
      omitirGuardado.current = true;
      localStorage.removeItem(CLAVE_CARRITO);
      setItems([]);
    }
  }, [listo, usuario?.id]);

  useEffect(() => {
    if (!listo || usuario || omitirGuardado.current) {
      omitirGuardado.current = false;
      return;
    }
    localStorage.setItem(CLAVE_CARRITO, JSON.stringify(items));
  }, [items, usuario, listo]);

  const agregar = (producto: Producto, cantidad = 1) => {
    if (producto.stock < 1) return;
    const pedida = Math.max(1, Math.floor(cantidad));
    const actual = items.find((item) => item.id === producto.id);
    const item: ItemCarrito = {
      id: producto.id,
      nombre: producto.nombre,
      precio: producto.precio,
      imagen: producto.imagen,
      cantidad: Math.min((actual?.cantidad ?? 0) + pedida, producto.stock),
      stock: producto.stock,
    };
    setItems((anteriores) =>
      anteriores.some((e) => e.id === producto.id)
        ? anteriores.map((e) => (e.id === producto.id ? item : e))
        : [...anteriores, item],
    );
    if (usuario) void guardarItemCarrito(item);
  };

  const cambiarCantidad = (id: number, cantidad: number) => {
    const actual = items.find((item) => item.id === id);
    if (!actual) return;
    const tope = actual.stock > 0 ? actual.stock : 99;
    const item: ItemCarrito = { ...actual, cantidad: Math.min(Math.max(1, Math.floor(cantidad)), tope) };
    setItems((anteriores) => anteriores.map((e) => (e.id === id ? item : e)));
    if (usuario) void guardarItemCarrito(item);
  };

  const eliminar = (id: number) => {
    setItems((anteriores) => anteriores.filter((item) => item.id !== id));
    if (usuario) void eliminarItemCarrito(id);
  };

  const vaciar = () => {
    setItems([]);
    if (usuario) void vaciarCarrito();
  };

  const totalItems = items.reduce((suma, item) => suma + item.cantidad, 0);
  const total = items.reduce((suma, item) => suma + item.precio * item.cantidad, 0);

  return (
    <CarritoContexto.Provider value={{ items, totalItems, total, agregar, cambiarCantidad, eliminar, vaciar }}>
      {children}
    </CarritoContexto.Provider>
  );
}

export function useCarrito(): CarritoContextoTipo {
  const contexto = useContext(CarritoContexto);
  if (!contexto) {
    throw new Error("useCarrito debe usarse dentro de <CarritoProvider>");
  }
  return contexto;
}
