create table mensaje (
  id bigint not null auto_increment primary key,
  nombre varchar(100) not null,
  correo varchar(100) not null,
  comentario varchar(500) not null,
  creado timestamp not null
);
