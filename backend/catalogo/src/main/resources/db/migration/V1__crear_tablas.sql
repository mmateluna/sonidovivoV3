-- Tablas del microservicio de catálogo.
create table producto (
  id bigint not null auto_increment primary key,
  codigo varchar(20) not null,
  nombre varchar(100) not null,
  marca varchar(80),
  modelo varchar(80),
  precio int not null,
  categoria varchar(80) not null,
  stock int not null,
  stock_critico int not null,
  imagen varchar(300),
  descripcion varchar(500),
  constraint uk_producto_codigo unique (codigo)
);
