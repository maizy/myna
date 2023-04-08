--liquibase formatted sql

--changeset myna:1
create table rulesets (
  id uuid not null primary key,
  name text
);

--rollback drop table rulesets
