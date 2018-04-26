import { TorunamentMatch, Team, PlayedMatch } from '../../types/types';
import { PlayerService } from '../../services/player-service';
import { WallService } from '../../services/wall.service';

export namespace TounamentService {
  let matches: Array<TorunamentMatch> = [];
  let numberOfTeams: number = 0;
  let allTeams: Array<Team> = [];

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

  function setWinnerForMatch(winnerTeamId: string, looserTeamId: string): void {
    const playedMatch = matches.find(match =>
      this.playsInMatch(match, winnerTeamId) && this.playsInMatch(match, looserTeamId));

    if (playedMatch && !playedMatch.winner) {
      playedMatch.winner = playedMatch.team1!!.id === winnerTeamId ? 'team1' : 'team2';
      
      const nextRoundIndex = this.getNextRoundIndex(playedMatch.id);
      if (!matches[nextRoundIndex].team1) {
        matches[nextRoundIndex].team1 = this.getTeamForId(winnerTeamId);
      } else {
        matches[nextRoundIndex].team2 = this.getTeamForId(winnerTeamId);
      }
    }
  }

  export function init(): void {
    PlayerService.getAllTeams()
      .then(teams => allTeams = teams)
      .then(() => {
          numberOfTeams = 16;
          matches = [
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
            { id: 15 },
          ];
        }
      );

    WallService.playedMatches().subscribe(newMatches => {
      // tslint:disable-next-line:no-console
      console.log(`Found ${newMatches.length} new matches!`);
      newMatches.forEach(match => setWinnerForMatch(getWinnerTeamId(match), getLooserTeamId(match)));
    });
  }

  export function getMatches(): Array<TorunamentMatch> {
    return matches;
  }
}