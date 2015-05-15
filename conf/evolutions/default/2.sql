# --- Sample dataset

# --- !Ups

insert into project (id,name,version) values ( 1,'Play',1.5);
insert into project (id,name,version) values ( 2,'Scala',1.4);
insert into project (id,name,version) values ( 3,'Bootstrap',1.3);
insert into project (id,name,version) values ( 4,'JavaScript',1.1);
insert into project (id,name,version) values ( 5,'CSS',1.2);

insert into person (id, first_name, last_name, website, email, is_active, date_joined, project_id) values ( 1,'Alicja','Kowalska', 'https://translate.google.pl/','kowalski@wp.pl', TRUE, '2014-12-01',1);
insert into person (id, first_name, last_name, website, email, is_active, date_joined, project_id) values ( 2,'Jan','Nowak', 'http://forum.4programmers.net/','nowak@wp.pl', TRUE, '2014-12-01',2);
insert into person (id, first_name, last_name, website, email, is_active, date_joined, project_id) values ( 3,'Janusz','Nowak', 'https://github.com','nowak@wp.pl',TRUE, '2014-12-01',3);
insert into person (id, first_name, last_name, website, email, is_active, date_joined, project_id) values ( 4,'Paulina','Kowalska','http://localhost:9000/','kowalska@pl.pl',TRUE, '2014-12-01',4);
insert into person (id, first_name, last_name, website, email, is_active, date_joined) values (5,'Natalia','Kowalska','http://localhost:9000/','kowalska@pl.pl',TRUE, '2014-12-01');

insert into task (id, name, project_id, person_id) values ( 1, 'task 1', 1, 1);
insert into task (id, name, project_id, person_id) values ( 2, 'task 2', 1, 2);
insert into task (id, name, project_id, person_id) values ( 3, 'task 1', 2, 3);
insert into task (id, name, project_id, person_id) values ( 4, 'task 1', 3, 4);
insert into task (id, name, project_id, person_id) values ( 5, 'task 1', 4, 5);


# --- !Downs

delete from person;
delete from project;
delete from task;