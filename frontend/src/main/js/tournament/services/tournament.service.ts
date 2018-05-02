import { TorunamentMatch, Team, PlayedMatch } from '../../types/types';
import { PlayerService } from '../../services/player-service';
import { WallService } from '../../services/wall.service';
import { BehaviorSubject, Observable } from 'rxjs';

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

  function getTeamForTeamName(teamName: string): Team | undefined {
    return allTeams.find(team => team.name === teamName);
  }

  function getTeamForPlayers(players: {player1: string, player2: string}): Team | undefined {
    return allTeams.find(team =>
      team.player1.id === players.player1 && team.player2.id === players.player2 ||
      team.player1.id === players.player2 && team.player2.id === players.player1);
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

  function setWinnerForMatch(winnerTeamId: string, looserTeamId: string): void {
    const matches = matchesSubject.value;
    const playedMatch = matches.find(match =>
      playsInMatch(match, winnerTeamId) && playsInMatch(match, looserTeamId));

    if (playedMatch && !playedMatch.winner) {
      playedMatch.winner = playedMatch.team1!!.id === winnerTeamId ? 'team1' : 'team2';

      const nextRoundIndex = getNextRoundIndex(playedMatch.id);
      if (nextRoundIndex < matches.length) {
        if (!matches[nextRoundIndex].team1) {
          matches[nextRoundIndex].team1 = getTeamForId(winnerTeamId);
        } else {
          matches[nextRoundIndex].team2 = getTeamForId(winnerTeamId);
        }
      }

      matchesSubject.next(matches);
    }
  }

  export function init(): void {
    if (!initialized) {
      initialized = true;

      PlayerService.getAllTeams()
        .then(teams => allTeams = teams)
        .then(() => {
            numberOfTeams = 16;
            matchesSubject.next([
              { id: 1, team1: getTeamForTeamName('foo1'), team2: getTeamForTeamName('bar1') },
              { id: 2, team1: getTeamForTeamName('foo2'), team2: getTeamForTeamName('bar2') },
              { id: 3, team1: getTeamForTeamName('foo3'), team2: getTeamForTeamName('bar3') },
              { id: 4, team1: getTeamForTeamName('foo4'), team2: getTeamForTeamName('bar4') },
              { id: 5, team1: getTeamForTeamName('foo5'), team2: getTeamForTeamName('bar5') },
              { id: 6, team1: getTeamForTeamName('foo6'), team2: getTeamForTeamName('bar6') },
              { id: 7, team1: getTeamForTeamName('foo7'), team2: getTeamForTeamName('bar7') },
              { id: 8, team1: getTeamForTeamName('foo8'), team2: getTeamForTeamName('bar8') },
              { id: 9 },
              { id: 10 },
              { id: 11 },
              { id: 12 },
              { id: 13 },
              { id: 14 },
              { id: 15, team1: getTeamForTeamName('foo8'), team2: getTeamForTeamName('bar8') },
            ]);
          }
        );

      WallService.playedMatches().subscribe(newMatches => {
        newMatches.forEach(match => setWinnerForMatch(getWinnerTeamId(match), getLooserTeamId(match)));
      });
    }
  }

  export function getTournamentWinner(): string | undefined {
    const matches = matchesSubject.value;
    const winner = matches.length > 0 ? matches[matches.length - 1].winner : undefined;
    return !winner ? undefined : matches[matches.length - 1][winner]!!.name;
  }
}
