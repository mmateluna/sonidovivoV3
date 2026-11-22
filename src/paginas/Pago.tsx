import { useState, type FormEvent } from "react";
import Boton from "../componentes/atomos/Boton";
import BotonLink from "../componentes/atomos/BotonLink";
import { useAuth } from "../contexto/AuthContext";
import { useCarrito } from "../contexto/CarritoContext";
import { crearPedido } from "../servicios/api";
import { formatearPrecio } from "../utilidades/formato";

const METODOS = [
  { id: "debito", etiqueta: "Tarjeta de débito" },
  { id: "credito", etiqueta: "Tarjeta de crédito" },
  { id: "transferencia", etiqueta: "Transferencia" },
];

function Pago() {
  const { usuario } = useAuth();
  const { items, total, vaciar } = useCarrito();
  const [metodo, setMetodo] = useState("");
  const [error, setError] = useState("");
  const [guardado, setGuardado] = useState(false);
  const [comprobante, setComprobante] = useState<{ metodo: string; total: number } | null>(null);

  if (comprobante) {
    return (
      <div className="container py-4">
        <h2>Pago realizado</h2>
        <div className="alert alert-success" role="alert">
          Pago simulado exitoso con {comprobante.metodo}. Total: {formatearPrecio(comprobante.total)}.
        </div>
        <p>
          {guardado
            ? "El pedido quedó guardado. No se hizo ningún cargo real. El carrito quedó vacío."
            : "No se hizo ningún cargo real. El carrito quedó vacío."}
        </p>
        <BotonLink to="/catalogo">Volver al catálogo</BotonLink>
      </div>
    );
  }

  if (items.length === 0) {
    return (
      <div className="container py-4">
        <h2>Pago</h2>
        <p>No hay productos para pagar.</p>
        <BotonLink to="/catalogo">Ir al catálogo</BotonLink>
      </div>
    );
  }

  const pagar = async (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault();
    const elegido = METODOS.find((opcion) => opcion.id === metodo);
    if (!elegido) {
      setError("Elige un medio de pago.");
      return;
    }
    if (usuario) {
      try {
        const pedido = await crearPedido(elegido.id);
        setGuardado(true);
        setComprobante({ metodo: pedido.metodo, total: pedido.total });
        vaciar();
      } catch (fallo) {
        setError(fallo instanceof Error ? fallo.message : "No se pudo registrar el pago.");
      }
      return;
    }
    setComprobante({ metodo: elegido.etiqueta, total });
    vaciar();
  };

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-12 col-lg-7">
          <h2>Pago</h2>
          <p className="mb-4">Esta pantalla simula una pasarela de pago. Revisa el pedido, elige cómo pagar y confirma.</p>

          <ul className="list-group mb-3">
            {items.map((item) => (
              <li className="list-group-item d-flex justify-content-between" key={item.id}>
                <span>
                  {item.nombre} × {item.cantidad}
                </span>
                <span>{formatearPrecio(item.precio * item.cantidad)}</span>
              </li>
            ))}
            <li className="list-group-item d-flex justify-content-between fw-bold">
              <span>Total</span>
              <span>{formatearPrecio(total)}</span>
            </li>
          </ul>

          <form onSubmit={pagar}>
            <p className="fw-semibold">Medio de pago</p>
            {METODOS.map((opcion) => (
              <div className="form-check" key={opcion.id}>
                <input
                  className="form-check-input"
                  type="radio"
                  name="metodo"
                  id={`metodo-${opcion.id}`}
                  checked={metodo === opcion.id}
                  onChange={() => {
                    setMetodo(opcion.id);
                    setError("");
                  }}
                />
                <label className="form-check-label" htmlFor={`metodo-${opcion.id}`}>
                  {opcion.etiqueta}
                </label>
              </div>
            ))}
            {error && <p className="text-danger mt-2">{error}</p>}
            <div className="d-flex flex-wrap gap-2 mt-3">
              <Boton type="submit">Realizar el pago</Boton>
              <BotonLink to="/carrito" variante="secondary">
                Volver al carrito
              </BotonLink>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default Pago;
