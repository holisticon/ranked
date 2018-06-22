import { TorunamentMatch, Team, PlayedMatch, NonPlayingTeamId } from '../../types/types';
import { PlayerService } from '../../services/player-service';
import { WallService } from '../../services/wall.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { createEmptyTeam } from '../../game/store.state';

export namespace TournamentService {
  let initialized: boolean = false;
  let matchesSubject: BehaviorSubject<Array<TorunamentMatch>> = new BehaviorSubject([]);
  let numberOfTeams: number = 0;
  let allTeams: Array<Team> = [];

  export function getAllMatches(): Observable<Array<TorunamentMatch>> {
    return matchesSubject;
  }

  export function getOpenMatches(): Observable<Array<TorunamentMatch>> {
    return matchesSubject.map(matches => matches.filter(match => !!match.team1 && !!match.team2 && !match.winner));
  }

  function getTeamForId(teamId: string): Team | undefined {
    return allTeams.find(team => team.id === teamId);
  }

  function getTeamForPlayers(players: { player1: string, player2: string }): Team | undefined {
    return allTeams.find(team =>
      team.player1.id === players.player1 && team.player2.id === players.player2 ||
      team.player1.id === players.player2 && team.player2.id === players.player1);
  }

  function createNonPlayingTeam(): Team {
    const team = createEmptyTeam();
    team.id = NonPlayingTeamId;
    return team;
  }

  function getWinnerTeamId(match: PlayedMatch): string {
    const team = getTeamForPlayers(match[match.winner]);
    return team ? team.id || '' : '';
  }

  function getLooserTeamId(match: PlayedMatch): string {
    const looser = match.winner === 'team1' ? 'team2' : 'team1';
    const team = getTeamForPlayers(match[looser]);
    return team ? team.id || '' : '';
  }

  export function playsInMatch(match: TorunamentMatch, teamId: string): boolean {
    const isTeam1 = match.team1 ? match.team1.id === teamId : false;
    const isTeam2 = match.team2 ? match.team2.id === teamId : false;
    return isTeam1 || isTeam2;
  }

  export function getNextRoundIndex(matchId: number): number {
    return Math.ceil(matchId / 2 + numberOfTeams / 2) - 1;
  }

  function evaluatePlayedMatch(newMatch: PlayedMatch): void {
    const winnerTeamId = getWinnerTeamId(newMatch);
    const looserTeamId = getLooserTeamId(newMatch);
    const matches = matchesSubject.value;
    const playedMatch = matches.find(match =>
      playsInMatch(match, winnerTeamId) && playsInMatch(match, looserTeamId));

    if (playedMatch && !playedMatch.winner) {
      playedMatch.winner = playedMatch.team1!!.id === winnerTeamId ? 'team1' : 'team2';
      playedMatch.team1Goals = newMatch.team1.goals;
      playedMatch.team2Goals = newMatch.team2.goals;

      matchesSubject.next(progressWinnerForMatch(matches, playedMatch.id, winnerTeamId));
    }
  }

  function progressWinnerForMatch(
    matches: Array<TorunamentMatch>, matchId: number, winnerTeamId: string
  ): Array<TorunamentMatch> {
    const nextRoundIndex = getNextRoundIndex(matchId);
    if (nextRoundIndex < matches.length) {
      if (!matches[nextRoundIndex].team1) {
        matches[nextRoundIndex].team1 = getTeamForId(winnerTeamId);
      } else {
        matches[nextRoundIndex].team2 = getTeamForId(winnerTeamId);
      }
    }
    return matches;
  }

  export function init(): void {
    if (!initialized) {
      initialized = true;

      PlayerService.getAllTeams()
        .then(teams => allTeams = teams)
        .then(() => {
          numberOfTeams = 16;
          let initialMatches = [
            { id: 1, team1: allTeams[7], team2: allTeams[12] },
            { id: 2, team1: allTeams[0], team2: allTeams[8] },
            { id: 3, team1: allTeams[11], team2: allTeams[4] },
            { id: 4, team1: allTeams[3], team2: allTeams[15] },
            { id: 5, team1: allTeams[13], team2: allTeams[5] },
            { id: 6, team1: allTeams[9], team2: allTeams[2] },
            { id: 7, team1: allTeams[14], team2: allTeams[1] },
            { id: 8, team1: allTeams[10], team2: allTeams[6] },
            { id: 9 },
            { id: 10 },
            { id: 11 },
            { id: 12 },
            { id: 13 },
            { id: 14 },
            { id: 15 },
          ] as Array<TorunamentMatch>;

          if (allTeams.length !== numberOfTeams) {
            for (let i = 0; i < numberOfTeams / 2; i++) {
              let match = initialMatches[i];
              if (!!match.team1 && !match.team2 || !match.team1 && !!match.team2) {
                let winnerTeamId = '';
                if (!match.team2) {
                  winnerTeamId = match.team1!!.id!!;
                  match.team2 = createNonPlayingTeam();
                  match.winner = 'team1';
                } else {
                  winnerTeamId = match.team2!!.id!!;
                  match.team1 = createNonPlayingTeam();
                  match.winner = 'team2';
                }

                initialMatches = progressWinnerForMatch(initialMatches, match.id, winnerTeamId);
              }
            }
          }
          matchesSubject.next(initialMatches);
        }
        );

      WallService.playedMatches().subscribe(newMatches => {
        newMatches.forEach(match => evaluatePlayedMatch(match));
      });
    }
  }

  export function getTournamentWinner(): Team | undefined {
    const matches = matchesSubject.value;
    const winner = matches.length > 0 ? matches[matches.length - 1].winner : undefined;
    return !winner ? undefined : matches[matches.length - 1][winner];
  }
}
