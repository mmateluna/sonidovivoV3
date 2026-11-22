# Abre los tres microservicios, cada uno en su ventana.
# Si esta PC tiene el JDK local de la evaluación, lo usa. Si no, java y mvn deben estar en el PATH.

$jdk = Join-Path $env:LOCALAPPDATA "jdks\jdk-21.0.13.1+2"
$mvn = Join-Path $env:LOCALAPPDATA "jdks\apache-maven-3.9.9\bin"
$raiz = Split-Path -Parent $MyInvocation.MyCommand.Path
$prefijo = ""
if (Test-Path $jdk) {
  $prefijo = "`$env:JAVA_HOME='$jdk'; `$env:Path='$jdk\bin;$mvn;' + `$env:Path; "
}

foreach ($modulo in @("catalogo", "cuentas", "carrito")) {
  $comando = $prefijo + "Set-Location '$raiz'; mvn -pl $modulo spring-boot:run"
  Start-Process powershell -ArgumentList "-NoExit", "-Command", $comando
}
