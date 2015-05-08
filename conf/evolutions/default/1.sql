# --- First database schema

# --- !Ups

set ignorecase true;

create table project (
  id            bigint not null,
  name          varchar(255) not null,
  version       decimal(10,4) not null,
  constraint pk_project primary key (id))
;

create table person (
  id            bigint not null,
  first_name    varchar(255) not null,
  last_name     varchar(255) not null,
  website       varchar(255) not null,
  email         varchar(255) not null,
  is_active     BOOLEAN,
  date_joined   timestamp,
  project_id    bigint,
  constraint pk_person primary key (id))
;

create sequence project_seq start with 1000;
create sequence person_seq start with 1000;

alter table person add constraint fk_person_project foreign key (project_id) references project(id) on delete restrict on update restrict;

create index ix_person_project on person (project_id);

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists project;
drop table if exists person;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists project_seq;
drop sequence if exists person_seq;
