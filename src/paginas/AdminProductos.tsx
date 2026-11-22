import { useEffect, useRef, useState } from "react";
import TablaProductosAdmin from "../componentes/organismos/TablaProductosAdmin";
import FormularioProducto from "../componentes/organismos/FormularioProducto";
import {
  actualizarProducto,
  crearProducto,
  eliminarProducto,
  listarProductos,
} from "../servicios/api";
import type { DatosProducto } from "../utilidades/validaciones";
import type { Producto } from "../tipos";

// Lo que se guarda aquí es lo que ve el catálogo: misma API, mismos campos.
function productoDesdeFormulario(datos: DatosProducto): Omit<Producto, "id"> {
  return {
    codigo: datos.codigo.trim(),
    categoria: datos.categoria,
    nombre: datos.nombre.trim(),
    descripcion: datos.descripcion.trim(),
    precio: Math.round(Number(datos.precio)),
    imagen: datos.imagen.trim(),
    stock: Number(datos.stock),
    stockCritico: datos.stockCritico.trim() === "" ? 0 : Number(datos.stockCritico),
    marca: (datos.marca ?? "").trim(),
    modelo: (datos.modelo ?? "").trim(),
  };
}

function AdminProductos() {
  const [productosAdmin, setProductosAdmin] = useState<Producto[]>([]);
  const [idEditando, setIdEditando] = useState<number | null>(null);
  const [claveFormulario, setClaveFormulario] = useState<number | "nuevo">("nuevo");
  const [versionFormulario, setVersionFormulario] = useState(0);
  const [error, setError] = useState("");
  const seccionFormulario = useRef<HTMLDivElement>(null);

  const productoEditando =
    idEditando === null ? null : productosAdmin.find((producto) => producto.id === idEditando) ?? null;

  const recargar = async () => setProductosAdmin(await listarProductos());

  useEffect(() => {
    recargar().catch(() => setError("No se pudieron cargar los productos."));
  }, []);

  const editar = (id: number) => {
    setIdEditando(id);
    setClaveFormulario(id);
    seccionFormulario.current?.scrollIntoView?.({ behavior: "smooth" });
  };

  const guardar = async (datos: DatosProducto) => {
    const campos = productoDesdeFormulario(datos);

    if (idEditando === null) {
      const nuevo = await crearProducto(campos);
      setIdEditando(nuevo.id);
    } else {
      await actualizarProducto(idEditando, campos);
    }
    setError("");
    await recargar();
  };

  const eliminar = async (id: number) => {
    try {
      await eliminarProducto(id);
      if (idEditando === id) {
        setIdEditando(null);
        setClaveFormulario("nuevo");
        setVersionFormulario((version) => version + 1);
      }
      setError("");
      await recargar();
    } catch (fallo) {
      setError(fallo instanceof Error ? fallo.message : "No se pudo eliminar el producto.");
    }
  };

  const prepararNuevo = () => {
    setIdEditando(null);
    setClaveFormulario("nuevo");
  };

  return (
    <div className="container py-4">
      <h2 className="mb-3">Gestión de productos</h2>
      <p className="mb-3">Esta lista es el catálogo de la tienda. Crear, editar o eliminar cambia lo que ve el cliente.</p>
      {error && <p className="text-danger">{error}</p>}
      <TablaProductosAdmin productos={productosAdmin} onEditar={editar} onEliminar={eliminar} />

      <div ref={seccionFormulario} className="mt-5">
        <h2 className="mb-3">
          {productoEditando ? `Editar producto: ${productoEditando.nombre}` : "Agregar producto"}
        </h2>
        <FormularioProducto
          key={`${claveFormulario}-${versionFormulario}`}
          producto={typeof claveFormulario === "number" ? productosAdmin.find((producto) => producto.id === claveFormulario) ?? null : null}
          onGuardar={guardar}
          onLimpiar={prepararNuevo}
        />
      </div>
    </div>
  );
}

export default AdminProductos;
