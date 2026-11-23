-- Tabla de cuentas. Los tokens de sesión viven en el servicio de auth.
create table usuario (
  id bigint not null auto_increment primary key,
  run varchar(12) not null,
  nombre varchar(50) not null,
  apellidos varchar(100) not null,
  correo varchar(100) not null,
  clave varchar(80) not null,
  rol varchar(20) not null,
  region varchar(80),
  comuna varchar(80),
  direccion varchar(300),
  fecha_nacimiento varchar(10),
  constraint uk_usuario_run unique (run),
  constraint uk_usuario_correo unique (correo)
);
