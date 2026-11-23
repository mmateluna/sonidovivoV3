create table pago (
  id bigint not null auto_increment primary key,
  usuario_id bigint not null,
  pedido_id bigint not null,
  metodo varchar(40) not null,
  total int not null,
  creado timestamp not null
);
