set mode MySQL;

create table if not exists BoardState (
  user_id varchar(90) not null unique,
  grid varchar(512) not null,
  is_player_turn int(2) not null,
  length int(2) not null
);
ALTER TABLE BoardState ADD PRIMARY KEY (user_id)
