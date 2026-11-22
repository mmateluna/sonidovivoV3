import { beforeEach, describe, expect, test, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import FormularioLogin from "../componentes/organismos/FormularioLogin";
import { AuthProvider } from "../contexto/AuthContext";

vi.mock("../servicios/api", async () => {
  return await import("./apiMock");
});

function renderizar() {
  return render(
    <AuthProvider>
      <FormularioLogin />
    </AuthProvider>,
  );
}

describe("FormularioLogin", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test("valida correo y contraseña obligatorios", async () => {
    const usuario = userEvent.setup();
    renderizar();

    await usuario.click(screen.getByRole("button", { name: "Ingresar" }));

    expect(screen.getByText("El correo es obligatorio.")).toBeInTheDocument();
    expect(screen.getByText("La contraseña es obligatoria.")).toBeInTheDocument();
  });

  test("rechaza un correo con dominio no permitido", async () => {
    const usuario = userEvent.setup();
    renderizar();

    await usuario.type(screen.getByLabelText("Correo"), "laura@hotmail.com");
    await usuario.type(screen.getByLabelText("Contraseña"), "1234");
    await usuario.click(screen.getByRole("button", { name: "Ingresar" }));

    expect(
      screen.getByText("Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com."),
    ).toBeInTheDocument();
  });

  test("rechaza una contraseña de menos de 4 caracteres", async () => {
    const usuario = userEvent.setup();
    renderizar();

    await usuario.type(screen.getByLabelText("Correo"), "laura@duoc.cl");
    await usuario.type(screen.getByLabelText("Contraseña"), "123");
    await usuario.click(screen.getByRole("button", { name: "Ingresar" }));

    expect(screen.getByText("La contraseña debe tener entre 4 y 10 caracteres.")).toBeInTheDocument();
  });

  test("con datos correctos muestra el mensaje de bienvenida", async () => {
    const usuario = userEvent.setup();
    renderizar();

    await usuario.type(screen.getByLabelText("Correo"), "laura@duoc.cl");
    await usuario.type(screen.getByLabelText("Contraseña"), "1234");
    await usuario.click(screen.getByRole("button", { name: "Ingresar" }));

    expect(await screen.findByText("Sesión iniciada correctamente. ¡Bienvenido/a!")).toBeInTheDocument();
    expect(screen.queryByLabelText("Correo")).not.toBeInTheDocument();
    expect(screen.queryByLabelText("Contraseña")).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Ingresar" })).not.toBeInTheDocument();
  });
});
