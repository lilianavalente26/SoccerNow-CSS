/*-- Sequences (Hibernate normalmente gera uma por tabela com AUTO)
create sequence achievement_seq start with 1 increment by 50;
create sequence card_seq start with 1 increment by 50;
create sequence clubs_seq start with 1 increment by 50;
create sequence matches_seq start with 1 increment by 50;
create sequence match_stats_seq start with 1 increment by 50;
create sequence stadium_seq start with 1 increment by 50;
create sequence teams_seq start with 1 increment by 50;
create sequence tournament_seq start with 1 increment by 50;
create sequence users_seq start with 1 increment by 50;

-- Tabela para herança SINGLE_TABLE entre User, Player e Referee
create table users (
    user_id bigint not null,
    name varchar(255),
    has_certificate boolean, -- só usado por Referee
    preferred_position varchar(255), -- só usado por Player
    dtype varchar(31), -- discriminator
    primary key (user_id)
);

create table clubs (
    club_id bigint not null,
    name varchar(255),
    primary key (club_id)
);

create table stadium (
    stadium_id bigint not null,
    stadium_name varchar(255) not null,
    primary key (stadium_id)
);

create table tournament (
    tournament_id bigint not null,
    name varchar(255),
    is_over boolean,
    point_system varchar(255), -- só se aplica a PointsTournament
    dtype varchar(31),
    primary key (tournament_id)
);

create table achievements (
    achievement_id bigint not null,
    placement integer,
    tournament_id bigint not null,
    club_id bigint,
    primary key (achievement_id)
);

create table teams (
    team_id bigint not null,
    club_id bigint not null,
    goalkeeper_id bigint,
    primary key (team_id)
);

create table tournament_clubs (
    tournament_id bigint not null,
    club_id bigint not null,
    primary key (tournament_id, club_id)
);

create table matches (
    match_id bigint not null,
    date date,
    time time,
    principal_referee_id bigint not null,
    stadium_id bigint,
    team1_team_id bigint,
    team2_team_id bigint,
    tournament_id bigint,
    stats_match_statistics_id bigint unique,
    primary key (match_id)
);

create table users_matches (
    matches_match_id bigint not null,
    referees_user_id bigint not null,
    primary key (matches_match_id, referees_user_id)
);

create table match_stats (
    match_statistics_id bigint not null,
    team1_score integer,
    team2_score integer,
    winner_team integer,
    is_over boolean,
    primary key (match_statistics_id)
);

create table card (
    card_id bigint not null,
    card_type varchar(255) not null,
    player_id bigint not null,
    match_statistics_id bigint,
    primary key (card_id)
);

create table users_teams (
    players_user_id bigint not null,
    teams_team_id bigint not null,
    primary key (players_user_id, teams_team_id)
);

-- Foreign Keys
alter table achievements add constraint fk_achievements_tournament foreign key (tournament_id) references tournament;
alter table achievements add constraint fk_achievements_club foreign key (club_id) references clubs;

alter table teams add constraint fk_teams_club foreign key (club_id) references clubs;

alter table tournament_clubs add constraint fk_tournament_clubs_tournament foreign key (tournament_id) references tournament;
alter table tournament_clubs add constraint fk_tournament_clubs_club foreign key (club_id) references clubs;

alter table matches add constraint fk_matches_referee foreign key (principal_referee_id) references users;
alter table matches add constraint fk_matches_stadium foreign key (stadium_id) references stadium;
alter table matches add constraint fk_matches_team1 foreign key (team1_team_id) references teams;
alter table matches add constraint fk_matches_team2 foreign key (team2_team_id) references teams;
alter table matches add constraint fk_matches_tournament foreign key (tournament_id) references tournament;
alter table matches add constraint fk_matches_stats foreign key (stats_match_statistics_id) references match_stats;

alter table users_matches add constraint fk_users_matches_match foreign key (matches_match_id) references matches;
alter table users_matches add constraint fk_users_matches_referee foreign key (referees_user_id) references users;

alter table card add constraint fk_card_statistics foreign key (match_statistics_id) references match_stats;

alter table users_teams add constraint fk_users_teams_team foreign key (teams_team_id) references teams;
alter table users_teams add constraint fk_users_teams_user foreign key (players_user_id) references users;
*/