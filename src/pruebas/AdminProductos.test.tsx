import { beforeEach, describe, expect, test, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import AdminProductos from "../paginas/AdminProductos";

vi.mock("../servicios/api", async () => {
  return await import("./apiMock");
});

// Cada prueba parte de la semilla: el admin lee el repositorio, y el repositorio lee localStorage.
beforeEach(() => {
  localStorage.clear();
});

async function crearProductoDePrueba() {
  const usuario = userEvent.setup();
  await usuario.type(screen.getByLabelText("Código de producto"), "ZZ999");
  await usuario.selectOptions(screen.getByLabelText("Categoría"), "Baterías");
  await usuario.type(screen.getByLabelText("Nombre del producto"), "Producto de prueba");
  await usuario.type(screen.getByLabelText("URL de imagen (opcional)"), "/img/prueba.png");
  await usuario.type(screen.getByLabelText("Precio (CLP)"), "1000");
  await usuario.type(screen.getByLabelText("Stock"), "3");
  await usuario.click(screen.getByRole("button", { name: "Guardar producto" }));
}

describe("admin de productos", () => {
  test("crear un producto muestra la fila en la tabla", async () => {
    render(<AdminProductos />);

    await crearProductoDePrueba();

    expect(await screen.findByRole("row", { name: /Producto de prueba/ })).toBeInTheDocument();
  }, 20000);

  test("editar cambia el nombre en la tabla", async () => {
    const usuario = userEvent.setup();
    render(<AdminProductos />);
    await screen.findAllByRole("button", { name: "Editar" });

    await usuario.click(screen.getAllByRole("button", { name: "Editar" })[0]);
    const nombre = screen.getByLabelText("Nombre del producto");
    await usuario.clear(nombre);
    await usuario.type(nombre, "Guitarra editada");
    await usuario.click(screen.getByRole("button", { name: "Guardar producto" }));

    expect(await screen.findByRole("row", { name: /Guitarra editada/ })).toBeInTheDocument();
    expect(screen.queryByRole("row", { name: /Guitarra Acústica Folk/ })).not.toBeInTheDocument();
  }, 20000);

  test("eliminar quita la fila de la tabla", async () => {
    const usuario = userEvent.setup();
    render(<AdminProductos />);

    await screen.findByRole("row", { name: /Guitarra Acústica Folk/ });
    await usuario.click(screen.getAllByRole("button", { name: "Eliminar" })[0]);

    await waitFor(() => {
      expect(screen.queryByRole("row", { name: /Guitarra Acústica Folk/ })).not.toBeInTheDocument();
    });
  }, 20000);
});
