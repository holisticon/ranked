import * as React from 'react';
import './tournament-tree.css';
import { Team, TeamKey } from '../../types/types';
import { createEmptyTeam } from '../../game/store.state';
import { WallService } from '../../services/wall.service';

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
  constructor(props: any) {
    super(props);
    this.init();
  }

  private createTeam(teamName: string): Team {
    return { ...createEmptyTeam(), name: teamName, id: teamName };
  }

  private init(): void {
    setTimeout(
      () => {
        this.setState({
          numberOfTeams: 16,
          matches: [
            { id: 1, team1: this.createTeam('foo1'), team2: this.createTeam('bar1'), winner: 'team1' },
            { id: 2, team1: this.createTeam('foo2'), team2: this.createTeam('bar2'), winner: 'team2' },
            { id: 3, team1: this.createTeam('foo3'), team2: this.createTeam('bar3'), winner: 'team2' },
            { id: 4, team1: this.createTeam('foo4'), team2: this.createTeam('bar4'), winner: 'team2' },
            { id: 5, team1: this.createTeam('foo5'), team2: this.createTeam('bar5'), winner: 'team1' },
            { id: 6, team1: this.createTeam('foo6'), team2: this.createTeam('bar6') },
            { id: 7, team1: this.createTeam('foo7'), team2: this.createTeam('bar7'), winner: 'team2' },
            { id: 8, team1: this.createTeam('foo8'), team2: this.createTeam('bar8'), winner: 'team1' },
            { id: 9, team1: this.createTeam('foo1'), team2: this.createTeam('bar2'), winner: 'team2' },
            { id: 10, team1: this.createTeam('bar3'), team2: this.createTeam('bar4'), winner: 'team2' },
            { id: 11, team1: this.createTeam('foo5') },
            { id: 12, team1: this.createTeam('bar7'), team2: this.createTeam('foo8') },
            { id: 13, team1: this.createTeam('bar2'), team2: this.createTeam('bar4') },
            { id: 14 },
            { id: 15 },
            // { team1: this.createTeam('foo5'), team2: this.createTeam('bar7') },
          ]
        });
      },
      500);

    WallService.playedMatches().subscribe(matches => {
      // tslint:disable-next-line:no-console
      console.log(`Found ${matches.length} new matches!`);
    });
  }

  private playsInMatch(match: TorunamentMatch, teamId: string): boolean {
    const isTeam1 = match.team1 ? match.team1.id === teamId : false;
    const isTeam2 = match.team2 ? match.team2.id === teamId : false;
    return isTeam1 || isTeam2;
  }

  private getNextRoundWinner(matchId: number): string {
    const nextRoundIndex = Math.ceil(matchId / 2 + this.state.numberOfTeams / 2) - 1;
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
