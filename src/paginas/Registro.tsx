import { useNavigate, Link } from "react-router-dom";
import FormularioUsuario from "../componentes/organismos/FormularioUsuario";
import { useAuth } from "../contexto/AuthContext";
import type { DatosUsuario } from "../utilidades/validaciones";

function Registro() {
  const { registrarCuenta } = useAuth();
  const navigate = useNavigate();

  const guardar = async (datos: DatosUsuario) => {
    const mensaje = await registrarCuenta(datos);
    if (!mensaje) navigate("/");
    return mensaje;
  };

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-12 col-lg-8">
          <h2>Registro de cliente</h2>
          <p className="mb-4">Crea tu cuenta para comprar en Sonido Vivo. El registro público siempre crea un cliente.</p>
          <FormularioUsuario
            tiposPermitidos={["Cliente"]}
            tipoInicial="Cliente"
            textoBoton="Registrarme"
            claveObligatoria
            onGuardar={guardar}
          />
          <p className="mt-3">
            ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Registro;
