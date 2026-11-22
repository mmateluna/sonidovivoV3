import type { ChangeEvent } from "react";

interface SelectorCantidadProps {
  id: string;
  valor: number;
  maximo: number;
  onChange: (cantidad: number) => void;
}

// La cantidad se elige antes de agregar y también se puede corregir en el carrito.
function SelectorCantidad({ id, valor, maximo, onChange }: SelectorCantidadProps) {
  const cambiar = (evento: ChangeEvent<HTMLInputElement>) => {
    const numero = Number(evento.target.value);
    if (!Number.isInteger(numero)) return;
    const limitada = Math.min(Math.max(numero, 1), maximo);
    onChange(limitada);
  };

  return (
    <div>
      <label htmlFor={id} className="form-label small mb-1">
        Cantidad
      </label>
      <input
        id={id}
        type="number"
        className="form-control form-control-sm"
        style={{ width: "5.5rem" }}
        min={1}
        max={maximo}
        value={valor}
        onChange={cambiar}
      />
    </div>
  );
}

export default SelectorCantidad;
