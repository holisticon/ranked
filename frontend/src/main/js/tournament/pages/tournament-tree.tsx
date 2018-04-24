import * as React from 'react';
import './tournament-tree.css';
import { Team, TeamKey, PlayedMatch } from '../../types/types';
import { WallService } from '../../services/wall.service';
import { PlayerService } from '../../services/player-service';

type TorunamentMatch = {
  id: number,
  team1?: Team,
  team2?: Team,
  winner?: TeamKey
};

interface TournamentTreeState {
  numberOfTeams: number;
  matches: Array<TorunamentMatch>;
}

export class TournamentTree extends React.Component<any, TournamentTreeState> {
  private teams: Array<Team> = [];

  constructor(props: any) {
    super(props);
    this.init();
  }

  // private createTeam(teamName: string): Team {
  //   return { ...createEmptyTeam(), name: teamName, id: teamName };
  // }

  private getTeamForId(teamId: string): Team | undefined {
    return this.teams.find(team => team.id === teamId);
  }

  private getTeamForPlayers(players: {player1: string, player2: string}): Team | undefined {
    return this.teams.find(team => 
      team.player1.id === players.player1 && team.player2.id === players.player2 ||
      team.player1.id === players.player2 && team.player2.id === players.player1);
  }

  private getTeamForTeamName(teamName: string): Team | undefined {
    return this.teams.find(team => team.name === teamName);
  }

  private init(): void {
    PlayerService.getAllTeams()
      .then(teams => this.teams = teams)
      .then(() =>
        setTimeout(
          () => {
            this.setState({
              numberOfTeams: 16,
              matches: [
                { id: 1, team1: this.getTeamForTeamName('foo1'), team2: this.getTeamForTeamName('bar1') },
                { id: 2, team1: this.getTeamForTeamName('foo2'), team2: this.getTeamForTeamName('bar2') },
                { id: 3, team1: this.getTeamForTeamName('foo3'), team2: this.getTeamForTeamName('bar3') },
                { id: 4, team1: this.getTeamForTeamName('foo4'), team2: this.getTeamForTeamName('bar4') },
                { id: 5, team1: this.getTeamForTeamName('foo5'), team2: this.getTeamForTeamName('bar5') },
                { id: 6, team1: this.getTeamForTeamName('foo6'), team2: this.getTeamForTeamName('bar6') },
                { id: 7, team1: this.getTeamForTeamName('foo7'), team2: this.getTeamForTeamName('bar7') },
                { id: 8, team1: this.getTeamForTeamName('foo8'), team2: this.getTeamForTeamName('bar8') },
                { id: 9 },
                { id: 10 },
                { id: 11 },
                { id: 12 },
                { id: 13 },
                { id: 14 },
                { id: 15 },
              ]
            });
          },
          500)
      );

    WallService.playedMatches().subscribe(matches => {
      // tslint:disable-next-line:no-console
      console.log(`Found ${matches.length} new matches!`);
      matches.forEach(match => this.setWinnerForMatch(this.getWinnerTeamId(match), this.getLooserTeamId(match)));
    });
  }

  private getWinnerTeamId(match: PlayedMatch): string {
    const team = this.getTeamForPlayers(match[match.winner]);
    return team ? team.id || '' : '';
  }

  private getLooserTeamId(match: PlayedMatch): string {
    const looser = match.winner === 'team1' ? 'team2' : 'team1';
    const team = this.getTeamForPlayers(match[looser]);
    return team ? team.id || '' : '';
  }

  private setWinnerForMatch(winnerTeamId: string, looserTeamId: string): void {
    const matches = this.state.matches;
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

      this.setState({matches});
    }

  }

  private playsInMatch(match: TorunamentMatch, teamId: string): boolean {
    const isTeam1 = match.team1 ? match.team1.id === teamId : false;
    const isTeam2 = match.team2 ? match.team2.id === teamId : false;
    return isTeam1 || isTeam2;
  }

  private getNextRoundIndex(matchId: number): number {
    return Math.ceil(matchId / 2 + this.state.numberOfTeams / 2) - 1;
  }

  private getNextRoundWinner(matchId: number): string {
    const nextRoundIndex = this.getNextRoundIndex(matchId);
    const winner = nextRoundIndex < this.state.matches.length ? this.state.matches[nextRoundIndex].winner : null;
    return winner ? this.state.matches[nextRoundIndex][winner]!!.id!! : '';
  }

  private renderMatches(matches: Array<TorunamentMatch>, containerClasses: string) {
    return (
      <div className={containerClasses}>
        {
          matches.map((match, i) => {
            const relevantSibling = i % 2 === 0 ? i + 1 : i - 1;
            const matchIsOld = !!match.winner && !!matches[relevantSibling].winner;
            const containsNextRoundWinner = this.playsInMatch(match, this.getNextRoundWinner(match.id));

            let classes = 'tournament-match';
            classes += matchIsOld ? ' old' : '';
            classes += containsNextRoundWinner ? ' next-round-winner' : '';

            return (
              <div key="i" className={classes}>
                {
                  !match.team1 && !match.team2 ?
                    <div className="default">tbd</div> :
                    <div className="opponents">
                      <div className={'team-name ' + (match.winner === 'team1' ? 'winner' : '')}>
                        {match.team1 ? match.team1.name : ''}
                      </div>
                      <div className="separator">vs</div>
                      <div className={'team-name ' + (match.winner === 'team2' ? 'winner' : '')}>
                        {match.team2 ? match.team2.name : ''}
                      </div>
                    </div>
                }
              </div>
            );
          })
        }
      </div>
    );
  }

  private renderTopAndBottomMatches(matches: Array<TorunamentMatch>, containerClasses: string) {
    return (
      <div className={containerClasses}>
        {this.renderMatches(matches.slice(0, matches.length / 2), 'top')}
        {this.renderMatches(matches.slice(matches.length / 2), 'bottom')}
      </div>
    );
  }

  render() {
    if (!this.state || !this.state.matches) {
      return null;
    }

    return (
      <div className="tournament-tree">
        <div className="background">
          <div className="top" />
          <div className="bottom" />
        </div>
        {this.renderTopAndBottomMatches(this.state.matches.slice(0, 8), 'last-16')}
        {this.renderTopAndBottomMatches(this.state.matches.slice(8, 12), 'quarter-finals')}
        {this.renderTopAndBottomMatches(this.state.matches.slice(12, 14), 'semi-finals')}
        <div className="final">
          {this.renderMatches(this.state.matches.slice(-1), 'final-match')}
        </div>
      </div>
    );
  }
}
