/*-- Clubes
insert into clubs (club_id, name) values (1, 'Sporting Clube de Lisboa');
insert into clubs (club_id, name) values (2, 'Futebol Clube do Porto');

-- Estádios
insert into stadium (stadium_id, stadium_name) values (1, 'Estádio José Alvalade');
insert into stadium (stadium_id, stadium_name) values (2, 'Estádio do Dragão');

-- Users (Dtype distingue Player e Referee)
insert into users (user_id, name, has_certificate, preferred_position, dtype) values (1, 'João Silva', false, 'GOALKEEPER', 'Player');
insert into users (user_id, name, has_certificate, preferred_position, dtype) values (2, 'Rui Costa', true, null, 'Referee');
insert into users (user_id, name, has_certificate, preferred_position, dtype) values (3, 'André Gomes', false, 'DEFENDER', 'Player');
insert into users (user_id, name, has_certificate, preferred_position, dtype) values (4, 'Pedro Martins', true, null, 'Referee');

-- Equipas
insert into teams (team_id, club_id) values (1, 1);
insert into teams (team_id, club_id) values (2, 2);
insert into teams (team_id, club_id) values (3, 2);

-- Ligação equipa-jogador
insert into team_player (team_id, player_id) values (1, 1);
insert into team_player (team_id, player_id) values (2, 3);

-- Torneios
insert into tournament (tournament_id, name, is_over, point_system, dtype) values (1, 'Liga dos Campeões', false, 'DEFAULT', 'PointsTournament');
insert into tournament (tournament_id, name, is_over, point_system, dtype) values (2, 'Taça de Portugal', true, 'EQUAL_POINTS', 'PointsTournament');

-- Ligação torneio-clube (via equipa)
insert into tournament_clubs (tournament_id, club_id) values (1, 1);
insert into tournament_clubs (tournament_id, club_id) values (1, 2);
insert into tournament_clubs (tournament_id, club_id) values (2, 1);

-- Estatísticas de jogo
insert into match_stats (match_statistics_id, team1_score, team2_score, winner_team) values (1, 2, 1, 1);

-- Jogos
insert into matches (
    match_id, date, time, principal_referee_id,
    stadium_id, team1_team_id, team2_team_id,
    tournament_id, stats_match_statistics_id
) values (
    1, '2025-04-01', '19:00:00', 2,
    1, 1, 2,
    1, 1
);

-- Árbitros adicionais no jogo
insert into match_referee (match_id, referee_id) values (1, 4);

-- Cartões
insert into card (card_id, card_type, player_id, match_statistics_id) values (1, 'YELLOW', 1, 1);
insert into card (card_id, card_type, player_id, match_statistics_id) values (2, 'RED', 3, 1);

-- Achievements
insert into achievements (achievement_id, placement, tournament_id, club_id) values (1, 1, 1, 1);
insert into achievements (achievement_id, placement, tournament_id, club_id) values (2, 2, 1, 2);
*/





-- HELPS TO TEST FOR REGISTER USER and REFEREE

/*
insert into stadium (stadium_id, stadium_name) values (1, 'Estádio José Alvalade');

insert into clubs (club_id, name) values (4, 'Sporting Clube de Lisboa');
insert into clubs (club_id, name) values (5, 'Futebol Clube do Porto');

insert into teams (team_id, club_id, goalkeeper_id) values (3, 4, 8);
insert into teams (team_id, club_id, goalkeeper_id) values (4, 4, 8);
insert into teams (team_id, club_id, goalkeeper_id) values (5, 5, 8);

insert into match_stats (match_statistics_id, team1_score, team2_score, winner_team, is_over) values (2, 3, 4, 3, true);

insert into users (user_id, name, has_certificate, preferred_position, dtype) values (8, 'Rui Costa', true, null, 'Referee');

insert into matches (
    match_id, date, time, principal_referee_id,
    stadium_id, team1_team_id, team2_team_id,
    tournament_id, stats_match_statistics_id
) values (
    2, '2025-04-27', '12:00:00', 8,
    1, 3, 4,
    null, 2
);
*/