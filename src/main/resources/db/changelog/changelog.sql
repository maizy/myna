--liquibase formatted sql

--changeset myna:1
create table rulesets (
  id text not null primary key,
  ruleset jsonb
);
--rollback drop table rulesets

--changeset myna:2
create index rulesets_name_idx on rulesets ((ruleset ->> 'name'), id);
--rollback drop index rulesets_name_idx
