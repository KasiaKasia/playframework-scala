# --- First database schema

# --- !Ups

set ignorecase true;


create table person (
  id            bigint not null,
  first_name    varchar(255) not null,
  last_name     varchar(255) not null,
  website       varchar(255) not null,
  email         varchar(255) not null,
  constraint pk_person primary key (id))
;

create table project (
  id        bigint not null,
  name      varchar(255) not null,
  person_id bigint,
  constraint pk_project primary key (id))
;

create sequence person_seq start with 1000;
create sequence project_seq start with 1000;


alter table project add constraint fk_project_person foreign key (person_id) references person (id)
on delete restrict on update restrict;

create index ix_project_person on project (person_id);


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists person;
drop table if exists project;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists person_seq;
drop sequence if exists project_seq;

