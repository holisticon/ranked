import * as React from 'react';
import './tournament-tree.css';
import { TorunamentMatch } from '../../types/types';
import { TournamentService } from '../services/tournament.service';
import { PlayerIcon } from '../../components/player-icon';

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

  private renderMatches(matches: Array<TorunamentMatch>, containerClasses: string) {
    return (
      <div className={containerClasses}>
        {
          matches.map((match, i) => {
            return (
              <div key="i" className={'tournament-match' + (!match.winner ? '' : ' decided')}>
                {
                  !match.team1 && !match.team2 ?
                    <div className="default">tbd</div> :
                    <div className="opponents">
                      <div className={'team-name ' + (match.winner === 'team1' ? 'winner' : '')}>
                        <div className="image-container">
                          { match.team1 ?
                            <PlayerIcon click={ () => { return; } } img={match.team1.imageUrl} />
                            : null
                          }
                        </div>
                        <div className="name-container">
                          { match.team1 ? `${match.team1.name} (${match.team1Goals})` : '' }
                        </div>
                      </div>
                      <div><div className="separator">vs</div></div>
                      <div className={'team-name ' + (match.winner === 'team2' ? 'winner' : '')}>
                        <div className="image-container">
                          { match.team2 ?
                            <PlayerIcon click={ () => { return; } } img={match.team2.imageUrl} />
                            : null
                          }
                        </div>
                        <div className="name-container">
                          { match.team2 ? `${match.team2.name} (${match.team2Goals})` : '' }
                        </div>
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
