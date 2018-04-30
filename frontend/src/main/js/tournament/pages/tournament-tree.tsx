import * as React from 'react';
import './tournament-tree.css';
import { TorunamentMatch } from '../../types/types';
import { TournamentService } from '../services/tournament.service';

interface TournamentTreeState {
  matches: Array<TorunamentMatch>;
}

export class TournamentTree extends React.Component<any, TournamentTreeState> {
  constructor(props: any) {
    super(props);
    this.state = { matches: [] };

    TournamentService.init();

    TournamentService.getAllMatches().subscribe(matches => {
      this.setState({matches});
    });
  }

  private getNextRoundWinner(matchId: number): string {
    const nextRoundIndex = TournamentService.getNextRoundIndex(matchId);
    const winner = nextRoundIndex < this.state.matches.length ? this.state.matches[nextRoundIndex].winner : null;
    return winner ? this.state.matches[nextRoundIndex][winner]!!.id!! : '';
  }

  private renderMatches(matches: Array<TorunamentMatch>, containerClasses: string) {
    return (
      <div className={containerClasses}>
        {
          matches.map((match, i) => {
            const containsNextRoundWinner = TournamentService.playsInMatch(
              match, this.getNextRoundWinner(match.id));

            let classes = 'tournament-match';
            classes += !!match.winner ? ' decided' : '';
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
