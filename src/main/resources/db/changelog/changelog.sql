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

--changeset myna:3
create type game_state as enum ('created', 'upcomming', 'launched', 'finished');
--rollback drop type game_state

--changeset myna:4
create table games(
  id text not null primary key,
  ruleset_id text not null references rulesets(id),
  state game_state not null default 'created',
  created_at timestamp with time zone not null,
  finished_at timestamp with time zone,
  owner_uid text not null
);
--rollback drop table games

--changeset myna:5
create table game_players(
  game_id text not null references games(id),
  ruleset_player_id text not null,
  name text not null,
  uid text,
  join_key text,
  joined_at timestamp with time zone,
  ruleset_order int not null default 0,
  primary key (game_id, ruleset_player_id)
);
--rollback drop table game_players
