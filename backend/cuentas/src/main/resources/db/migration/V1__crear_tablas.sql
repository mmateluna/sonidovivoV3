-- Tablas del microservicio de cuentas.
-- Flyway ejecuta este archivo una vez y lo anota en flyway_schema_history.
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

create table token_refresco (
  id bigint not null auto_increment primary key,
  valor varchar(700) not null,
  usuario_id bigint not null,
  expira timestamp not null,
  constraint uk_token_valor unique (valor),
  constraint fk_token_usuario foreign key (usuario_id) references usuario (id)
);
