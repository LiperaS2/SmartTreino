create database SmartTreino;
use SmartTreino;
create table usuario(
nome varchar (70),
telefone varchar (15),
email varchar (60),
senha varchar (30),
termos varchar (4),
primary key (email)
);
use SmartTreino;
select * from  usuario;