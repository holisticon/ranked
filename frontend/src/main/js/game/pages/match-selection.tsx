import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { TorunamentMatch, Team } from '../../types/types';
import { push } from 'react-router-redux';
import './player-selection.css';
import { PartialStoreState } from '../store.state';
import { TournamentService } from '../../tournament/services/tournament.service';
import './match-selection.css';

export interface MatchSelectionProps {
  startMatch: (team1: Team, team2: Team) => void;
}

interface MatchSelectionState {
  openMatches: Array<TorunamentMatch>;
}

class MatchSelection extends React.Component<MatchSelectionProps, MatchSelectionState> {
  
  constructor(props: MatchSelectionProps) {
    super(props);
    this.state = { openMatches: [] };
    
    TournamentService.init();
    TournamentService.getOpenMatches()
      .subscribe(openMatches => this.setState({ openMatches }));
  }

  private renderOpenMatches() {
    return this.state.openMatches.map((match, i) => {
      return (
        <div key={i} className="open-match" onClick={ () => this.props.startMatch(match.team1!!, match.team2!!) } >
          <div className="team-name">
            {match.team1 ? match.team1.name : ''}
          </div>
          <div className="separator">vs</div>
          <div className="team-name">
            {match.team2 ? match.team2.name : ''}
          </div>
        </div>
      );
    });
  }

  public render() {
    if (!this.state || !this.state.openMatches) {
      return null;
    }

    let tournamentWinner = TournamentService.getTournamentWinner();

    return (
      <div className="match-selection">
        {
          !tournamentWinner ?
          this.renderOpenMatches() :
          <div className="tournament-finished">
            <div className="text">
              Team { tournamentWinner } hat das Turnier gewonnen! Bugs und Feedback gerne an O3 und Timo, danke 🙂.
            </div>
            <a className="link" href="http://docker.holisticon.local:11080">
              Zurück zum normalen Modus
            </a>
          </div>
        }
      </div>
    );
  }
}

export function mapStateToProps({ ranked: { } }: PartialStoreState) {
  return { };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    startMatch: (team1: Team, team2: Team) => {
      dispatch(Actions.selectEntity('team1'));
      dispatch(Actions.setTeam('team1', team1));
      dispatch(Actions.selectEntity('team2'));
      dispatch(Actions.setTeam('team2', team2));
      dispatch(push('/'));
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(MatchSelection);
