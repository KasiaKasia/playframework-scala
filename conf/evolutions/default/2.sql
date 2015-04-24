# --- Sample dataset

# --- !Ups

insert into person (id, first_name, last_name, website, email) values ( 1,'Alicja','Kowalska', 'https://translate.google.pl/','kowalski@wp.pl');
insert into person (id, first_name, last_name, website, email) values ( 2,'Jan','Nowak', 'http://forum.4programmers.net/','nowak@wp.pl');
insert into person (id, first_name, last_name, website, email) values ( 3,'Janusz','Nowak', 'https://github.com','nowak@wp.pl');
insert into person (id, first_name, last_name, website, email) values ( 4,'Paulina','Kowalska','http://localhost:9000/','kowalska@pl.pl');

insert into project (id,name,person_id) values ( 1,'Play',1);
insert into project (id,name,person_id) values ( 2,'Scala',2);
insert into project (id,name,person_id) values ( 3,'Bootstrap',3);
insert into project (id,name,person_id) values ( 4,'JavaScript',4);
# --- !Downs

delete from person;
delete from project;
