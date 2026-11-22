import { useState, type ChangeEvent, type FormEvent } from "react";
import CampoFormulario from "../moleculas/CampoFormulario";
import Boton from "../atomos/Boton";
import { useAuth } from "../../contexto/AuthContext";
import { validarLogin, hayErrores, type DatosLogin, type Errores } from "../../utilidades/validaciones";

function FormularioLogin() {
  const { iniciarSesion, usuario } = useAuth();
  const [datos, setDatos] = useState<DatosLogin>({ correo: "", clave: "" });
  const [errores, setErrores] = useState<Errores<DatosLogin>>({});

  const manejarCambio = (evento: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = evento.target;
    setDatos({ ...datos, [name]: value });
  };

  const manejarEnvio = async (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault();
    const nuevosErrores = validarLogin(datos);
    setErrores(nuevosErrores);
    if (hayErrores(nuevosErrores)) return;
    const mensaje = await iniciarSesion(datos.correo.trim(), datos.clave);
    if (mensaje) {
      setErrores({ correo: mensaje });
      return;
    }
  };

  if (usuario) {
    return (
      <div className="alert alert-success" role="alert">
        <p className="mb-1">Sesión iniciada correctamente. ¡Bienvenido/a!</p>
        <p className="mb-0">
          {usuario.nombre} {usuario.apellidos}. Entraste como {usuario.tipo}.
        </p>
      </div>
    );
  }

  return (
    <form onSubmit={manejarEnvio} noValidate>
      <CampoFormulario
        id="login-correo"
        etiqueta="Correo"
        nombre="correo"
        tipo="email"
        valor={datos.correo}
        onChange={manejarCambio}
        error={errores.correo}
        placeholder="tu@correo.cl"
        maxLength={100}
      />

      <CampoFormulario
        id="login-clave"
        etiqueta="Contraseña"
        nombre="clave"
        tipo="password"
        valor={datos.clave}
        onChange={manejarCambio}
        error={errores.clave}
        placeholder="4 a 10 caracteres"
        maxLength={10}
      />

      <Boton type="submit">Ingresar</Boton>
    </form>
  );
}

export default FormularioLogin;
