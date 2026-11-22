import { useEffect, useState } from "react";
import ListaProductos from "../componentes/organismos/ListaProductos";
import BotonLink from "../componentes/atomos/BotonLink";
import { listarProductos } from "../servicios/api";
import type { Producto } from "../tipos";

// Un producto de distintos rubros para la portada
const CODIGOS_DESTACADOS = ["GA001", "GE001", "BT001", "TC002"];

function Inicio() {
  const [destacados, setDestacados] = useState<Producto[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    listarProductos()
      .then((lista) => {
        setDestacados(
          CODIGOS_DESTACADOS.map((codigo) => lista.find((producto) => producto.codigo === codigo)).filter(
            (producto): producto is Producto => producto !== undefined,
          ),
        );
      })
      .catch(() => setError("No se pudieron cargar los destacados. Revisa que el backend esté en marcha."));
  }, []);

  return (
    <>
      <section className="banner d-flex align-items-center justify-content-center text-center">
        <div className="container py-5">
          <h1 className="display-5 fw-bold">Sonido Vivo</h1>
          <p className="lead mx-auto mb-4" style={{ maxWidth: "600px" }}>
            Tu tienda de instrumentos y equipos de audio en Viña del Mar. Más de 11 años acompañando a músicos de la
            región.
          </p>
          <BotonLink to="/catalogo">Ver catálogo</BotonLink>
        </div>
      </section>

      <section className="container py-5">
        <h2 className="mb-4">Productos destacados</h2>
        {error ? <p>{error}</p> : <ListaProductos productos={destacados} />}
      </section>
    </>
  );
}

export default Inicio;
