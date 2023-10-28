--liquibase formatted sql

--changeset myna:1
create table rulesets (
  id text not null primary key,
  name text,
  ruleset jsonb
);

--rollback drop table rulesets
